package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.transport.GsonTransport
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */


class JsonSerialziationTest {
  data class TestRequest(val time: LocalDateTime?)
  
  @Test
  fun serializeLocalDateTime() {
    val request = TestRequest(LocalDateTime.of(2017, 3, 4, 0, 0, 0))

    val bout = ByteArrayOutputStream()
    val transport = GsonTransport()

    transport.out(bout, TestRequest::class.java, request)

    assertThat(bout.toString(), equalTo("{\"time\":\"2017-03-04T00:00:00\"}"))
  }

  @Test
  fun serializeNullLocalDateTime() {
    val request = TestRequest(null)

    val bout = ByteArrayOutputStream()
    val transport = GsonTransport()

    transport.out(bout, TestRequest::class.java, request)

    assertThat(bout.toString(), equalTo("{}"))
  }

  @Test
  fun deserializeLocalDateTime() {
    val transport = GsonTransport();
    val source = "{\"time\":\"2017-03-04T00:00:00\"}";
    val request = transport.`in`(ByteArrayInputStream(source.toByteArray()), TestRequest::class.java)

    assertThat(request.time?.year, equalTo(2017))
    assertThat(request.time?.monthValue, equalTo(3))
    assertThat(request.time?.dayOfMonth, equalTo(4))
  }

  @Test(expected = DateTimeParseException::class)
  fun deserializeBadFormattedLocalDateTime() {
    val transport = GsonTransport();
    val source = "{\"time\":\"2017-03-04T0000\"}";
    transport.`in`(ByteArrayInputStream(source.toByteArray()), TestRequest::class.java)
  }

  @Test
  fun deserializeNullLocalDateTime() {
    val transport = GsonTransport();
    val source = "{\"time\":null}";
    val request = transport.`in`(ByteArrayInputStream(source.toByteArray()), TestRequest::class.java)

    assertEquals(request.time, null)
  }

}