package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.core.*
import com.clouway.pos.print.core.Receipt.newReceipt
import com.clouway.pos.print.printer.Status
import com.clouway.pos.print.transport.GsonTransport
import com.google.gson.GsonBuilder
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
      val dto = request.read(ReceiptDTO::class.java).`as`(GsonTransport::class.java)
      val printer = factory.getPrinter(dto.sourceIp)
      try {
        when {
          dto.fiscal
          -> response = printer.printFiscalReceipt(dto.receipt)
          else
          -> response = printer.printReceipt(dto.receipt)
        }
      } catch (e: RequestTimeoutException) {
        return Reply.with(ErrorResponse("Printer request timeout.\n" + e.message)).`as`(GsonTransport::class.java).status(504)
      } finally {
        printer.close()
      }
    } catch (e: DeviceNotFoundException) {
      return Reply.with(ErrorResponse("Device not found.")).`as`(GsonTransport::class.java).notFound()
    } catch (e: IOException) {
      return Reply.with(ErrorResponse("Device can't connect.")).`as`(GsonTransport::class.java).status(480)
    }

    val responseDTO: PrintReceiptResponseDTO = PrintReceiptResponseDTO(response.warnings)
    println(GsonBuilder().create().toJson(responseDTO))

    return if (responseDTO.warnings.contains(Status.FISCAL_RECEIPT_IS_OPEN) ||
      responseDTO.warnings.contains(Status.NON_FISCAL_RECEIPT_IS_OPEN)) {
      Reply.with(responseDTO).`as`(GsonTransport::class.java).ok()
    } else {
      Reply.with(response).`as`(GsonTransport::class.java).badRequest()
    }
  }

  internal data class ReceiptDTO(var sourceIp: String = "", var operatorId: String = "", var fiscal: Boolean = false, var receipt: Receipt = newReceipt().build())
  internal data class PrintReceiptResponseDTO(var warnings: Set<Status> = emptySet())
}