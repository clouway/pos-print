package com.clouway.pos.print.transport

import com.google.common.io.ByteStreams
import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.TypeLiteral
import com.google.sitebricks.client.Transport
import java.io.*

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
class GsonTransport @Inject
constructor(private val gson: Gson) : Transport {

  @Throws(IOException::class)
  override fun <T> `in`(input: InputStream, tClass: Class<T>): T {
    return gson.fromJson(createReader(input), tClass)
  }

  @Throws(IOException::class)
  override fun <T> `in`(input: InputStream, typeLiteral: TypeLiteral<T>): T {
    return gson.fromJson<T>(createReader(input), typeLiteral.type)
  }

  @Throws(IOException::class)
  override fun <T> out(outputStream: OutputStream, tClass: Class<T>, t: T) {
    outputStream.write(gson.toJson(t).toByteArray(charset("UTF8")))
  }

  override fun contentType(): String {
    return "application/json"
  }

  @Throws(IOException::class)
  private fun createReader(input: InputStream): InputStreamReader {
    val out = ByteArrayOutputStream()
    ByteStreams.copy(input, out)
    return InputStreamReader(ByteArrayInputStream(out.toByteArray()), "UTF-8")
  }
}