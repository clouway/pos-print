package com.clouway.pos.print.core

import java.io.IOException

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
interface PrinterFactory {

  /**
   * Finds registered Receipt Printer by sourceIp
   * @return Receipt Printer
   * @throws IOException in case of socket failure
   * @throws DeviceNotFoundException in case the device is not found
   */
  @Throws(DeviceNotFoundException::class, IOException::class)
  fun getPrinter(sourceIp: String): ReceiptPrinter
}

internal class DeviceNotFoundException : Throwable()