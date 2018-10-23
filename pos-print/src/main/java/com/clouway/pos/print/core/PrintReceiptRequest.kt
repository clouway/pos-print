package com.clouway.pos.print.core

/**
 * An object containing a receipt and information on who
 * sent the print request.
 *
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
data class PrintReceiptRequest(val receipt: Receipt,
                               val sourceIp: String,
                               val operatorId: String,
                               val isFiscal: Boolean)