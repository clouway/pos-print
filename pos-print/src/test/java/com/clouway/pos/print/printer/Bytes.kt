package com.clouway.pos.print.printer

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */

/**
 *  Returns the HEX representation of ByteArray data.
 */
internal fun ByteArray.toHexString(): String {
    return "[" + this.map { it -> String.format("0x%02X", it) }.toList().joinToString(",") + "]"
}
