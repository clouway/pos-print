package com.clouway.pos.print.core

import java.util.concurrent.ArrayBlockingQueue

/**
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
class InMemoryPrintQueue : PrintQueue {

  private val queueMax = 50

  private val queue = ArrayBlockingQueue<ReceiptWithStatus>(queueMax)

  override fun next(): ReceiptWithStatus {
    return queue.take()
  }

  override fun queue(receiptWithStatus: ReceiptWithStatus) {
    queue.offer(receiptWithStatus)
  }
}