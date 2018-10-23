package com.clouway.pos.print.core

/**
 * Provides the methods to give feedback after a print
 * has been attempted.
 *
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
interface PrintingListener {
  /**
   * Notifies when a printing status has been updated.
   *
   * @param receipt the updated receipt
   * @param printStatus the new status
   */
  fun onPrinted(receipt: Receipt, printStatus: PrintStatus)
}