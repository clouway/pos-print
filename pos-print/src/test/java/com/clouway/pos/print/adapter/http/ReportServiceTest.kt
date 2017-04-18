package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.ReplyMatchers.Companion.contains
import com.clouway.pos.print.ReplyMatchers.Companion.isBadRequest
import com.clouway.pos.print.ReplyMatchers.Companion.isOk
import com.clouway.pos.print.ReplyMatchers.Companion.isStatus
import com.clouway.pos.print.SiteBricksRequestMockery
import com.clouway.pos.print.core.*
import org.hamcrest.MatcherAssert.assertThat
import org.jmock.AbstractExpectations
import org.jmock.Expectations
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class ReportServiceTest {

  @Rule @JvmField
  var context = JUnitRuleMockery()

  private val factory = context.mock(PrinterFactory::class.java)
  private val printer = context.mock(ReceiptPrinter::class.java)

  private val request = SiteBricksRequestMockery()
  private val reportService = ReportService(factory)

  @Test
  fun printReportForOperator() {
    val operatorReportDTO = ReportService.OperatorReportDTO("CLEAR", "::any id::", "::any ip::")

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).reportForOperator("::any id::", RegisterState.CLEAR)
        oneOf(printer).close()
      }
    })

    val reply = reportService.printOperatorReport(request.mockRequest(operatorReportDTO))

    assertThat(reply, isOk)
    assertThat(reply, contains("Report completed"))
  }

  @Test
  fun missingCashRegisterForOperatorReport() {
    val operatorReportDTO = ReportService.OperatorReportDTO("KEEP", "::any id::", "::any ip::")

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(DeviceNotFoundException()))
      }
    })

    val reply = reportService.printOperatorReport(request.mockRequest(operatorReportDTO))

    assertThat(reply, isBadRequest)
    assertThat(reply, contains(ErrorResponse("Device not found.")))
  }

  @Test
  fun connectionFailureForOperatorReport() {
    val operatorReportDTO = ReportService.OperatorReportDTO("CLEAR", "::any id::", "::any ip::")

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(IOException()))
      }
    })

    val reply = reportService.printOperatorReport(request.mockRequest(operatorReportDTO))

    assertThat(reply, isStatus(480))
    assertThat(reply, contains(ErrorResponse("Device can't connect.")))
  }

  @Test
  fun printReportForPeriod() {
    val periodReportDTO = ReportService.PeriodReportDTO("2017-04-23T18:25:43.511Z", "2017-04-23T18:25:43.511Z", "::any ip::")

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).reportForPeriod(LocalDate.parse("2017-04-23T18:25:43.511Z", DateTimeFormatter.ISO_DATE_TIME), LocalDate.parse("2017-04-23T18:25:43.511Z", DateTimeFormatter.ISO_DATE_TIME))
        oneOf(printer).close()
      }
    })

    val reply = reportService.printPeriodReport(request.mockRequest(periodReportDTO))

    assertThat(reply, isOk)
    assertThat(reply, contains("Report completed"))
  }

  @Test
  fun missingCashRegisterForPeriodReport() {
    val periodReportDTO = ReportService.PeriodReportDTO("2017-04-23T18:25:43.511Z", "2017-04-23T18:25:43.511Z", "::any ip::")

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(DeviceNotFoundException()))
      }
    })

    val reply = reportService.printPeriodReport(request.mockRequest(periodReportDTO))

    assertThat(reply, isBadRequest)
    assertThat(reply, contains(ErrorResponse("Device not found.")))
  }

  @Test
  fun connectionFailureForPeriodReport() {
    val periodReportDTO = ReportService.PeriodReportDTO("2017-04-23T18:25:43.511Z", "2017-04-23T18:25:43.511Z", "::any ip::")

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(IOException()))
      }
    })

    val reply = reportService.printPeriodReport(request.mockRequest(periodReportDTO))

    assertThat(reply, isStatus(480))
    assertThat(reply, contains(ErrorResponse("Device can't connect.")))
  }
}
