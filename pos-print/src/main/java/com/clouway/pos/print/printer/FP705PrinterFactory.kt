package com.clouway.pos.print.printer

import com.clouway.pos.print.core.DeviceNotFoundException
import com.clouway.pos.print.core.PrinterFactory
import com.clouway.pos.print.adapter.db.CashRegisterRepository
import com.clouway.pos.print.core.FiscalPolicy
import com.google.inject.Inject
import java.net.Socket

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
class FP705PrinterFactory
@Inject constructor(private var repository: CashRegisterRepository) : PrinterFactory {

  override fun getPrinter(sourceIp: String): FP705Printer {
    val possibleRegister = repository.getBySourceIp(sourceIp)
    if (!possibleRegister.isPresent) {
      throw DeviceNotFoundException()
    }
    val register = possibleRegister.get()
    var host = register.destination
    var port = 4999

    if (host.contains(":")) {
      val parts = host.split(":")
      host = parts[0]
      port = Integer.valueOf(parts[1])
    }

    val fiscalPolicy: List<FiscalPolicy> = possibleRegister.get().fiscalPolicy

    val socket = Socket(host, port)
    return FP705Printer(socket.getInputStream(), socket.getOutputStream(), fiscalPolicy )
  }
}