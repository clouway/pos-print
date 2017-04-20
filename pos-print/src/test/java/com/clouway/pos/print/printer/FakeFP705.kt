package com.clouway.pos.print.printer

import com.google.common.util.concurrent.AbstractExecutionThreadService
import java.io.IOException
import java.net.ServerSocket

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */

class FakeFP705 : AbstractExecutionThreadService() {
    internal var s: ServerSocket = ServerSocket(0)
    internal var flows: ArrayList<Flow> = ArrayList()

    fun port(): Int {
        return s.localPort
    }

    /**
     * Prepares the next communication flow between device and the pos-print app.
     */
    fun prepareFlows(vararg flow: Flow) {
        flows.addAll(flow)
    }

    override fun run() {
        while (isRunning) {
            try {
                val c = s.accept()
                val b = ByteArray(2048)

                val input = c.getInputStream()
                val output = c.getOutputStream()
               
                for ((index, flow) in flows.withIndex()) {
                    val readBytes = input.read(b)

                    val requestedBytes = b.sliceArray(IntRange(0, readBytes - 1))

                    if (!requestedBytes.contentEquals(flow.request)) {
                        output.write(0x15)
                        c.close()
                        throw RuntimeException(
                                "Flow: " + index + "\n" +
                                        "expected request: " + flow.request.toHexString() + "\n," +
                                        "            got: " + requestedBytes.toHexString()
                        )
                    } else {
                        output.write(flow.response)
                        output.flush()
                    }
                }

                output.flush()
                c.close()
            } catch (e: IOException) {
                break
            }
        }
    }

    override fun triggerShutdown() {
        s.close()
    }

    data class Flow(val request: ByteArray, val response: ByteArray)

}