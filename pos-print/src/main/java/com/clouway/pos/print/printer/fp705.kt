package com.clouway.pos.print.printer

import java.util.*

/**
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */

/**
 * FM705Response is representing the response which is returned by the FM705 Devices of Datecs.
 * <p/>
 * The response message structure of FM705:
 * <01><LEN><SEQ><CMD><DATA><04><STATUS><05><BCC><03>
 *  1b   4b   1b   4b   n    1b    8b    1b   4b  1b
 */
internal class FP705Response(val content: ByteArray) : Response {
    internal val FIRST_DATA_BYTE_INDEX: Int = 10

    /**
     * Checks whether response of the operation is successful or not by using the first
     * byte from the data bytes.
     * <p/>
     * From Spec: Indicates an error code. If command passed, ErrorCode is 0;
     */
    override fun isSuccessful(): Boolean {
        /**
         * The first data byte.    `
         */
        val errorCode = content[FIRST_DATA_BYTE_INDEX]
        return errorCode.toChar() == '0'
    }

    override fun status(): ByteArray {
        return Arrays.copyOfRange(content, content.size - 14, content.size - 6)
    }

    /**
     * Gets data bytes of the packet. It skips the first byte and the separator \t as it's used to indicate
     * the status of the response whether it was successful or not.
     */
    override fun data(): ByteArray {
        // <01><LEN><SEQ><CMD><DATA><04><STATUS><05><BCC><03>
        // Range
        //      from: FIRST_DATA_BYTE_INDEX + ErrorCode + \t,
        //        to: size - 15 = 03 + BCC + 05 + STATUS + 04
        return Arrays.copyOfRange(content, FIRST_DATA_BYTE_INDEX + 2, content.size - 15)
    }

    /**
     * Gets the raw content of the response.
     */
    override fun raw(): ByteArray {
        return content
    }

}