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
      when {
        dto.fiscal
        -> response = printer.printFiscalReceipt(dto.receipt)
        else
        -> response = printer.printReceipt(dto.receipt)
      }
      printer.close()
    } catch (e: DeviceNotFoundException) {
      return Reply.with(ErrorResponse("Device not found.")).badRequest()
    } catch (e: IOException) {
      return Reply.with(ErrorResponse("Device can't connect.")).status(480)
    }
    val responseDTO: PrintReceiptResponseDTO = PrintReceiptResponseDTO(response.warnings)
    return Reply.with(responseDTO).`as`(GsonTransport::class.java)
  }


  internal data class ReceiptDTO(var sourceIp: String = "", var operatorId: String = "", var fiscal: Boolean = false, var receipt: Receipt = newReceipt().build())
  internal data class PrintReceiptResponseDTO(var warnings: Set<Status> = emptySet())
}