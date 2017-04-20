package com.clouway.pos.print.transport

import com.google.common.io.ByteStreams
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.google.inject.Inject
import com.google.inject.TypeLiteral
import com.google.sitebricks.client.Transport
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
class GsonTransport @Inject
constructor() : Transport {

  class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {

    override fun write(out: JsonWriter, value: LocalDateTime?) {
      if (value == null) {
        out.nullValue()
        return
      }
      out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }

    override fun read(reader: JsonReader): LocalDateTime? {
      if (reader.peek() == JsonToken.NULL) {
        reader.nextNull();
        return null;
      }

      val value = reader.nextString()
      return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value));
    }

  }

  internal val gson = GsonBuilder()
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
    .create()
  

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