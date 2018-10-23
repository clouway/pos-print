package com.clouway.pos.print.core

/**
 * An object wrapping a receipt with the information
 * of the sender and the reference to the persisted document.
 *
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
data class ReceiptWithStatus (val requestId: String,
                              val receipt: Receipt,
                              val operatorId: String,
                              val sourceIp: String,
                              val isFiscal: Boolean,
                              val printStatus: PrintStatus,
                              val creationSecond: Long)