package com.clouway.pos.print.core

import com.clouway.pos.print.printer.FP705Printer
import com.clouway.pos.print.printer.FP705PrinterFactory
import com.google.inject.AbstractModule

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
class CoreModule : AbstractModule() {

  override fun configure() {
    bind(PrinterFactory::class.java).to(FP705PrinterFactory::class.java)
  }
}