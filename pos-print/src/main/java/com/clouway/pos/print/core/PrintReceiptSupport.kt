package com.clouway.pos.print.core

import com.clouway.pos.print.printer.Status

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
data class PrintReceiptResponse(var warnings: Set<Status> = emptySet())