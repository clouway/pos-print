package com.clouway.pos.print.core

import java.util.Optional

/**
 * Provides the methods to persist, save and check the status
 * of requested receipts.
 *
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
interface ReceiptRepository {

  /**
   * Registers a receipt request.
   *
   * @param receiptRequest the receipt to register
   * @return the persisted receipt with status
   */
  @Throws(ReceiptAlreadyRegisteredException::class)
  fun register(receiptRequest: PrintReceiptRequest): ReceiptWithStatus

  /**
   * Returns a receipt and its status and sender information
   * by a given request id.
   *
   * @param requestId the id of the request to return.
   * @return a ReceiptWithStatus
   */
  fun getByRequestId(requestId: String): Optional<ReceiptWithStatus>

  /**
   * Returns the printing status of a receipt.
   *
   * @param receiptRequestId the id of the receipt request
   * @return the status of the receipt
   */
  @Throws(ReceiptNotRegisteredException::class)
  fun getStatus(receiptRequestId: String): PrintStatus

  /**
   * Returns the latest receipts up to a limit.
   *
   * @param limit The amount of receipts to return.
   * @return A map of receipts with their request ids as keys.
   */
  fun getLatest(limit: Int): List<ReceiptWithStatus>

  /**
   * Marks a receipt as Printed.
   *
   * @param requestId the id of the receipt to finish
   * @return the finished receipt
   */
  @Throws(ReceiptNotRegisteredException::class)
  fun finishPrinting(requestId: String): ReceiptWithStatus

  /**
   * Marks a receipt as Failed.
   *
   * @param requestId the id of the receipt to fail.
   * @return the rejected receipt
   */
  @Throws(ReceiptNotRegisteredException::class)
  fun failPrinting(requestId: String): ReceiptWithStatus
}


internal class ReceiptNotRegisteredException : Throwable()
internal class ReceiptAlreadyRegisteredException : Throwable()

enum class PrintStatus {
  PRINTING, PRINTED, FAILED
}