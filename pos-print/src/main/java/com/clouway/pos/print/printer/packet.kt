package com.clouway.pos.print.printer

/**
 * Packet is a kotlin package which contains request and response metadata.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
/**
 * Response is representing the response from the device.
 */
interface Response {

    /**
     * Checks whether response was successful or not.
     */
    fun isSuccessful(): Boolean

    /**
     * Gets data bytes of response.
     */
    fun data(): ByteArray

    /**
     * Gets status bytes of response.
     */
    fun status(): ByteArray

    /**
     * Gets raw bytes of the response.
     */
    fun raw(): ByteArray

}