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

class IOChannel(private var inputStream: InputStream, private var outputStream: OutputStream) : Channel {
  private val maxRetries: Int = 5
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

class WarningChannel(private val origin: Channel, private var statuses: Set<Status> = emptySet()) : Channel {

  override fun sendPacket(request: ByteArray): Response {
    val response: Response = origin.sendPacket(request)
    statuses.plus(decodeStatus(response.status()))
    return response
  }

  fun warnings(): Set<Status> {
    val warnings = Sets.newLinkedHashSet<Status>()
    warnings += statuses
    return warnings
  }

  private fun decodeStatus(status: ByteArray): Set<Status> {
    val result = Sets.newLinkedHashSet<Status>()
    Status.values().filterTo(result) { it.isSetIn(status) }
    return result
  }
}