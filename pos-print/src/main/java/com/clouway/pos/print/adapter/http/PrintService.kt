package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.core.*
import com.clouway.pos.print.core.Receipt.newReceipt
import com.clouway.pos.print.printer.Status
import com.clouway.pos.print.transport.GsonTransport
import com.google.sitebricks.At
import com.google.sitebricks.headless.Reply
import com.google.sitebricks.headless.Request
import com.google.sitebricks.headless.Service
import com.google.sitebricks.http.Post
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.logging.Logger
import javax.inject.Inject

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
@Service
@At("/v1/receipts/req/print")
class PrintService @Inject constructor(private var factory: PrinterFactory) {
  private val logger = LoggerFactory.getLogger(PrintService::class.java)

  @Post
  fun printReceipt(request: Request): Reply<*> {
    val response: PrintReceiptResponse
    try {
      val dto = request.read(ReceiptDTO::class.java).`as`(GsonTransport::class.java)
      val printer = factory.getPrinter(dto.sourceIp)
      response = try {
        when {
          dto.fiscal
          -> printer.printFiscalReceipt(dto.receipt)
          else
          -> printer.printReceipt(dto.receipt)
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

    if (response.warnings.isNotEmpty()) {
      logger.info("Got Warnings: ${response.warnings}")
    }

    val responseDTO = PrintReceiptResponseDTO(response.warnings)

    return if (responseDTO.warnings.contains(Status.FISCAL_RECEIPT_IS_OPEN) ||
      responseDTO.warnings.contains(Status.NON_FISCAL_RECEIPT_IS_OPEN)) {
      Reply.with(responseDTO).`as`(GsonTransport::class.java).ok()
    } else {
      Reply.with(responseDTO).`as`(GsonTransport::class.java).badRequest()
    }
  }

  internal data class ReceiptDTO(val sourceIp: String = "", val operatorId: String = "", val fiscal: Boolean = false, val receipt: Receipt = newReceipt().build())
  internal data class PrintReceiptResponseDTO(var warnings: Set<Status> = emptySet())
}