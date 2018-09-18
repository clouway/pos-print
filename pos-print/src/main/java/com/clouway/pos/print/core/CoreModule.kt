package com.clouway.pos.print.core

import com.clouway.pos.print.printer.FP705PrinterFactory
import com.google.inject.AbstractModule
import com.google.inject.Singleton

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
class CoreModule : AbstractModule() {

  override fun configure() {
    bind(PrinterFactory::class.java).to(FP705PrinterFactory::class.java)
    bind(PrintQueue::class.java).to(InMemoryPrintQueue::class.java).`in`(Singleton::class.java)
    bind(PrintingListener::class.java).to(ReceiptPrintingListener::class.java).`in`(Singleton::class.java)
  }
}