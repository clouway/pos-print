package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.core.Receipt
import com.clouway.pos.print.core.Receipt.newReceipt
import com.clouway.pos.print.core.DeviceNotFoundException
import com.clouway.pos.print.core.ErrorResponse
import com.clouway.pos.print.core.PrinterFactory
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
@At("/v1/posprint")
class PrintService @Inject constructor(private var factory: PrinterFactory) {

  @Post
  fun printReceipt(request: Request): Reply<*> {
    try {
      val dto = request.read(ReceiptDTO::class.java).`as`(GsonTransport::class.java)
      val printer = factory.getPrinter(dto.sourceIp)
      when {
        dto.fiscal
        -> printer.printFiscalReceipt(dto.receipt)
        else
        -> printer.printReceipt(dto.receipt)
      }
      printer.close()
    } catch (e: DeviceNotFoundException) {
      return Reply.with(ErrorResponse("Device not found.")).badRequest()
    } catch (e: IOException) {
      return Reply.with(ErrorResponse("Device can't connect.")).status(480)
    }
    return Reply.with("OK")
  }

  internal data class ReceiptDTO(var sourceIp: String = "", var operatorId: String = "", var fiscal: Boolean = false, var receipt: Receipt = newReceipt().build())
}