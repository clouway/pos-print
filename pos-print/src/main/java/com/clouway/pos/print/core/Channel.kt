package com.clouway.pos.print.core

import com.clouway.pos.print.printer.FP705Response
import com.clouway.pos.print.printer.Response
import com.clouway.pos.print.printer.Status
import com.google.common.base.Ascii.SYN
import com.google.common.collect.Sets
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.SocketTimeoutException


/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
interface Channel {
  @Throws(IOException::class)
  fun sendPacket(request: ByteArray): Response
}

class IOChannel(val inputStream: InputStream, val outputStream: OutputStream, val maxRetries: Int = 5) : Channel {

  override fun sendPacket(request: ByteArray): Response {
    val sink: BufferedSink = Okio.buffer(Okio.sink(outputStream))

    println("========================")
    print("Request: ")
    request.forEach { print(String.format("0x%02X, ", it)) }
    println()

    sink.write(request)
    sink.flush()

    val source: BufferedSource = Okio.buffer(Okio.source(inputStream))
    val buffer: Buffer = Buffer()

    for (retry in 0..maxRetries) {
      try {
        source.read(buffer, 2048)

        if (buffer.indexOf(SYN) > 0) {
          println("Got Sync")
        }

        val from = buffer.indexOf(0x01)
        val to = buffer.indexOf(0x03)

        if (from >= 0 && to > 0) {
          buffer.skip(from)
          val content = buffer.readByteArray(to - from + 1)
          return FP705Response(content)
        }

        Thread.sleep(200)

      } catch (e: SocketTimeoutException) {
        try {
          Thread.sleep(5000)
        } catch (e: InterruptedException) {
          e.printStackTrace()
        }
      }
    }

    throw RequestTimeoutException(String.format("unable to get response after %d retries", maxRetries))
  }
}

/**
 * WarningChannel is a channel which collects status of the device after each packet that was send to the origin channel.
 * <p/>
 * After all operations are executed, callers are able to use the warnings() func to get all warnings that are happened
 * during the execution of the operations.
 * <p/>
 * This is how code is simplified by using of this class:
 * <code>
 *   response = channel.sendPacket(request1)
 *   statuses = statuses.plus(decode(response.status()))
 *   ...
 *   response = channel.sendPacket(request2)
 *   statuses = statuses.plus(decode(response.status()))
 * </code>
 *
 * vs
 *
 * <code>
 *  channel.sendPacket(request1)
 *  channel.sendPacket(request2)
 *   ...
 *  warnings = channel.warnings()
 * </code>
 *
 *
 */
class WarningChannel(private val origin: Channel, private var statuses: Set<Status> = emptySet()) : Channel {

  override fun sendPacket(request: ByteArray): Response {
    val response: Response = origin.sendPacket(request)
    statuses = statuses.plus(decodeStatus(response.status()))
    return response
  }

  fun warnings(): Set<Status> {
    return statuses.filter { it.isForWarning }.toSet()
  }

  internal fun decodeStatus(status: ByteArray): Set<Status> {
    val result = Sets.newLinkedHashSet<Status>()
    Status.values().filterTo(result) { it.isSetIn(status) }
    return result
  }
}