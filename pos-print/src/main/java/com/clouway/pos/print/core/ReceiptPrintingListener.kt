package com.clouway.pos.print.core

import org.slf4j.LoggerFactory

/**
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
class ReceiptPrintingListener : PrintingListener {
  private val logger = LoggerFactory.getLogger(PrintingListener::class.java)

  override fun onPrinted(receipt: Receipt, printStatus: PrintStatus) {
    logger.info("Receipt was processed")
    logger.info(receipt.receiptId)
    logger.info(printStatus.name)
  }
}