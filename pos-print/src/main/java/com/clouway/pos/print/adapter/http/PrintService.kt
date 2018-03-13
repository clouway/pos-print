package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.core.*
import com.clouway.pos.print.core.Receipt.newReceipt
import com.clouway.pos.print.core.ReceiptItem.newItem
import com.clouway.pos.print.printer.Status
import com.clouway.pos.print.transport.GsonTransport
import com.google.sitebricks.At
import com.google.sitebricks.headless.Reply
import com.google.sitebricks.headless.Request
import com.google.sitebricks.headless.Service
import com.google.sitebricks.http.Post
import java.io.IOException
import javax.inject.Inject

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
@Service
@At("/v1/receipts/req/print")
class PrintService @Inject constructor(private var factory: PrinterFactory) {

  @Post
  fun printReceipt(request: Request): Reply<*> {
    val response: PrintReceiptResponse
    try {
      val dto = request.read(PrintReceiptRequestDTO::class.java).`as`(GsonTransport::class.java)
      val receipt = dto.receipt.adapt()
      val printer = factory.getPrinter(dto.sourceIp)
      try {
        response = when {
          dto.fiscal
          -> printer.printFiscalReceipt(receipt)
          else
          -> printer.printReceipt(receipt)
        }
      } catch (e: RequestTimeoutException) {
        return Reply.with(ErrorResponse("Printer request timeout.\n${e.message}")).`as`(GsonTransport::class.java).status(504)
      } finally {
        printer.close()
      }
    } catch (e: DeviceNotFoundException) {
      return Reply.with(ErrorResponse("Device not found.")).`as`(GsonTransport::class.java).notFound()
    } catch (e: IOException) {
      return Reply.with(ErrorResponse("Device can't connect.")).`as`(GsonTransport::class.java).status(480)
    }

    val responseDTO = PrintReceiptResponseDTO(response.warnings)

    return if (responseDTO.warnings.contains(Status.FISCAL_RECEIPT_IS_OPEN) ||
      responseDTO.warnings.contains(Status.NON_FISCAL_RECEIPT_IS_OPEN)) {
      Reply.with(responseDTO).`as`(GsonTransport::class.java).ok()
    } else {
      Reply.with(response).`as`(GsonTransport::class.java).badRequest()
    }
  }

  internal data class PrintReceiptRequestDTO(val sourceIp: String = "", val operatorId: String = "", val fiscal: Boolean = false, val receipt: ReceiptDTO = ReceiptDTO())

  internal data class ReceiptDTO(@JvmField val receiptId: String = "",
                                 @JvmField val prefixLines: List<String> = listOf(),
                                 @JvmField val receiptItems: List<ReceiptItemDTO> = listOf(),
                                 @JvmField val suffixLines: List<String> = listOf(),
                                 @JvmField var currency: String = "BGN",
                                 @JvmField var amount: Double = 0.00) {
    fun adapt(): Receipt {
      return newReceipt()
        .withReceiptId(receiptId)
        .prefixLines(prefixLines)
        .addItems(receiptItems.map { it.asReceiptItem() })
        .suffixLines(suffixLines)
        .currency(currency)
        .withAmount(amount)
        .build()
    }
  }

  internal data class ReceiptItemDTO(@JvmField val name: String = "",
                                     @JvmField val quantity: Double = 1.0,
                                     @JvmField val price: Double = 0.0,
                                     @JvmField val vat: Double = 0.0,
                                     @JvmField val department: String = "0"
  ) {
    fun asReceiptItem(): ReceiptItem {
      return newItem().name(this.name).quantity(this.quantity).price(this.price).vat(this.vat).department(department).build()
    }
  }

  internal data class PrintReceiptResponseDTO(var warnings: Set<Status> = emptySet())
}