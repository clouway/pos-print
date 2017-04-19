package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.core.*
import com.clouway.pos.print.transport.GsonTransport
import com.google.inject.Inject
import com.google.sitebricks.At
import com.google.sitebricks.client.transport.Json
import com.google.sitebricks.headless.Reply
import com.google.sitebricks.headless.Request
import com.google.sitebricks.headless.Service
import com.google.sitebricks.http.Post
import java.io.IOException
import java.time.LocalDateTime

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
@Service
@At("/v1/reports")
class ReportService @Inject constructor(private var factory: PrinterFactory) {

  @Post
  @At("/operator/print")
  fun printOperatorReport(request: Request): Reply<*> {
    try {
      val operatorReportDTO = request.read(OperatorReportDTO::class.java).`as`(GsonTransport::class.java)

      val printer = factory.getPrinter(operatorReportDTO.sourceIp)

      printer.reportForOperator(operatorReportDTO.operatorId, adaptToState(operatorReportDTO.registerState))
      printer.close()

    } catch (e: DeviceNotFoundException) {
      return Reply.with(ErrorResponse("Device not found.")).`as`(GsonTransport::class.java).badRequest()
    } catch (e: IOException) {
      return Reply.with(ErrorResponse("Device can't connect.")).`as`(GsonTransport::class.java).status(480)
    }
    return Reply.with("Report completed").`as`(Json::class.java).ok()
  }

  @Post
  @At("/periodical/print")
  fun printPeriodReport(request: Request): Reply<*> {
    try {
      val periodReportDTO = request.read(PeriodReportDTO::class.java).`as`(GsonTransport::class.java)

      val printer = factory.getPrinter(periodReportDTO.sourceIp)

      printer.reportForPeriod(periodReportDTO.from, periodReportDTO.to, adaptToType(periodReportDTO.periodType))
      printer.close()

    } catch (e: DeviceNotFoundException) {
      return Reply.with(ErrorResponse("Device not found.")).`as`(GsonTransport::class.java).badRequest()
    } catch (e: IOException) {
      return Reply.with(ErrorResponse("Device can't connect.")).`as`(GsonTransport::class.java).status(480)
    }
    return Reply.with("Report completed").`as`(Json::class.java).ok()
  }

  private fun adaptToState(state: String): RegisterState {
    if (state.equals("CLEAR")) {
      return RegisterState.CLEAR
    }
    return RegisterState.KEEP
  }

  private fun adaptToType(type: String): PeriodType {
    if (type.equals("SHORT")) {
      return PeriodType.SHORT
    }
    return PeriodType.EXTENDED
  }

  internal data class OperatorReportDTO(var registerState: String = "", var operatorId: String = "", var sourceIp: String = "")
  internal data class PeriodReportDTO(var from: LocalDateTime = LocalDateTime.now(), var to: LocalDateTime = LocalDateTime.now(), var sourceIp: String = "", var periodType: String = "")
}