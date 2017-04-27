package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.JsonBuilder.Companion.aNewJson
import com.clouway.pos.print.ReplyMatchers.Companion.contains
import com.clouway.pos.print.ReplyMatchers.Companion.isBadRequest
import com.clouway.pos.print.ReplyMatchers.Companion.isOk
import com.clouway.pos.print.ReplyMatchers.Companion.isStatus
import com.clouway.pos.print.FakeRequest.Factory.newJsonRequest
import com.clouway.pos.print.core.*
import org.hamcrest.MatcherAssert.assertThat
import org.jmock.AbstractExpectations
import org.jmock.Expectations
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class ReportServiceTest {

  @Rule @JvmField
  var context = JUnitRuleMockery()

  private val factory = context.mock(PrinterFactory::class.java)
  private val printer = context.mock(ReceiptPrinter::class.java)

  private val reportService = ReportService(factory)

  @Test
  fun printReportForOperator() {
    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).reportForOperator("::any id::", RegisterState.CLEAR)
        oneOf(printer).close()
      }
    })

    val reply = reportService.printOperatorReport(newJsonRequest(aNewJson().add("registerState", "CLEAR").add("operatorId", "::any id::").add("sourceIp", "::any ip::").build()))

    assertThat(reply, isOk)
    assertThat(reply, contains("Report completed"))
  }

  @Test
  fun missingCashRegisterForOperatorReport() {
    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(DeviceNotFoundException()))
      }
    })

    val reply = reportService.printOperatorReport(newJsonRequest(aNewJson().add("sourceIp", "::any ip::").build()))

    assertThat(reply, isBadRequest)
    assertThat(reply, contains(ErrorResponse("Device not found.")))
  }

  @Test
  fun connectionFailureForOperatorReport() {
    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(IOException()))
      }
    })

    val reply = reportService.printOperatorReport(newJsonRequest(aNewJson().add("sourceIp", "::any ip::").build()))

    assertThat(reply, isStatus(480))
    assertThat(reply, contains(ErrorResponse("Device can't connect.")))
  }

  @Test
  fun printReportForPeriod() {
    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).reportForPeriod(LocalDateTime.parse("2017-01-01T00:00:00.000Z", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2017-01-02T00:00:00.000Z", DateTimeFormatter.ISO_DATE_TIME), PeriodType.SHORT)
        oneOf(printer).close()
      }
    })

    val reply = reportService.printPeriodReport(newJsonRequest(aNewJson().add("sourceIp", "::any ip::").add("from", "2017-01-01T00:00:00.000Z").add("to", "2017-01-02T00:00:00.000Z").add("periodType", "SHORT").build()))

    assertThat(reply, isOk)
    assertThat(reply, contains("Report completed"))
  }

  @Test
  fun missingCashRegisterForPeriodReport() {
    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(DeviceNotFoundException()))
      }
    })

    val reply = reportService.printPeriodReport(newJsonRequest(aNewJson().add("sourceIp", "::any ip::").build()))

    assertThat(reply, isBadRequest)
    assertThat(reply, contains(ErrorResponse("Device not found.")))
  }

  @Test
  fun connectionFailureForPeriodReport() {
    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(IOException()))
      }
    })

    val reply = reportService.printPeriodReport(newJsonRequest(aNewJson().add("sourceIp", "::any ip::").build()))

    assertThat(reply, isStatus(480))
    assertThat(reply, contains(ErrorResponse("Device can't connect.")))
  }
}
