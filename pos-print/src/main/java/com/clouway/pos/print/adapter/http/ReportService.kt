package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.core.DeviceNotFoundException
import com.clouway.pos.print.core.ErrorResponse
import com.clouway.pos.print.core.PrinterFactory
import com.clouway.pos.print.core.RegisterState
import com.clouway.pos.print.transport.GsonTransport
import com.google.sitebricks.At
import com.google.sitebricks.client.transport.Json
import com.google.sitebricks.headless.Reply
import com.google.sitebricks.headless.Request
import com.google.sitebricks.headless.Service
import com.google.sitebricks.http.Post
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
@Service
@At("/v1/reports")
class ReportService(private var factory: PrinterFactory) {

  @Post
  @At("/operator/print")
  fun printOperatorReport(request: Request): Reply<*> {
    try {
      val operatorReportDTO = request.read(OperatorReportDTO::class.java).`as`(GsonTransport::class.java)
      val printer = factory.getPrinter(operatorReportDTO.sourceIp)

      printer.reportForOperator(operatorReportDTO.operatorId, adapt(operatorReportDTO.registerState))
      printer.close()

    } catch (e: DeviceNotFoundException) {
      return Reply.with(ErrorResponse("Device not found.")).badRequest()
    } catch (e: IOException) {
      return Reply.with(ErrorResponse("Device can't connect.")).status(480)
    }
    return Reply.with("Report completed").`as`(Json::class.java).ok()
  }

  @Post
  @At("/periodical/print")
  fun printPeriodReport(request: Request): Reply<*> {
    try {
      val periodReportDTO = request.read(PeriodReportDTO::class.java).`as`(GsonTransport::class.java)
      val printer = factory.getPrinter(periodReportDTO.sourceIp)

      printer.reportForPeriod(adaptToDate(periodReportDTO.from), adaptToDate(periodReportDTO.to))
      printer.close()

    } catch (e: DeviceNotFoundException) {
      return Reply.with(ErrorResponse("Device not found.")).badRequest()
    } catch (e: IOException) {
      return Reply.with(ErrorResponse("Device can't connect.")).status(480)
    }
    return Reply.with("Report completed").`as`(Json::class.java).ok()
  }

  private fun adapt(state: String): RegisterState {
    if (state.equals("CLEAR")) {
      return RegisterState.CLEAR
    }
    return RegisterState.KEEP
  }

  private fun adaptToDate(date: String): LocalDate {
    return LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME)
  }

  internal data class OperatorReportDTO(var registerState: String = "", var operatorId: String = "", var sourceIp: String = "")
  internal data class PeriodReportDTO(var from: String = "", var to: String = "", var sourceIp: String = "")
}