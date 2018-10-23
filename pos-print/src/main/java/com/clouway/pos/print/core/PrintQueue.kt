package com.clouway.pos.print.core

/**
 * Provides the methods to iterate over and append to a
 * queue of receipts.
 *
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
interface PrintQueue {

  /**
   * Returns the next receipt.
   */
  fun next(): ReceiptWithStatus?

  /**
   * Queues a receipt along with its status.
   *
   * @param receiptWithStatus the receipt to queue
   */
  fun queue(receiptWithStatus: ReceiptWithStatus)
}