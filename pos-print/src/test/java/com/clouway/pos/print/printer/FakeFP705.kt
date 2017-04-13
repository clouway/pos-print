package com.clouway.pos.print.printer

import com.google.common.util.concurrent.AbstractExecutionThreadService
import org.junit.Assert.fail
import java.io.IOException
import java.net.ServerSocket

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */

class FakeFP705 : AbstractExecutionThreadService() {
    internal var s: ServerSocket = ServerSocket(5555)
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
        while (isRunning()) {
            try {
                val c = s.accept()
                val b = ByteArray(2048)

                val input = c.getInputStream();
                val output = c.getOutputStream()

                var count = 0
                for (f in flows) {
                    val readBytes = input.read(b)

                    val requestedBytes = b.sliceArray(IntRange(0, readBytes - 1))

                    if (!requestedBytes.contentEquals(f.request)) {
                        fail("expected request: " + f.request.toHexString() + "\n , got: " + requestedBytes.toHexString())
                        output.write(0x15)
                        c.close()
                        break
                    } else {
                        output.write(f.response)
                        output.flush()
                    }

                    count++;
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