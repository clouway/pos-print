package com.clouway.servicebroker.service;

import com.clouway.servicebroker.print.PrintService;
import com.evo.servicebroker.client.FinancialReportRequest;
import com.evo.servicebroker.client.PrintResponse;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@At("/printFinancialReport")
@Service
public class PrintFiscalReportService {
  private final Logger log = Logger.getLogger(PrintFiscalReportService.class.getName());

  private final Provider<FinancialReportRequest> dailyFinancialReportRequestProvider;
  private final Provider<PrintService> printService;

  @Inject
  public PrintFiscalReportService(Provider<FinancialReportRequest> dailyFinancialReportRequestProvider,
                                  Provider<PrintService> printService) {
    this.dailyFinancialReportRequestProvider = dailyFinancialReportRequestProvider;
    this.printService = printService;
  }

  @Get
  @Post
  public Reply<PrintResponse> printDailyFinancialReport() {

    try {
      FinancialReportRequest request = dailyFinancialReportRequestProvider.get();

      printService.get().connect();

      if (FinancialReportRequest.Type.DAILY_REPORT.equals(request.getType())) {

        printService.get().printDepartmentDailyReport(request.getClearDailyTurnover());

      } else if (FinancialReportRequest.Type.PERIOD_REPORT.equals(request.getType())) {

        printService.get().printPeriodReport(request.getStart(), request.getEnd());

      } else if (FinancialReportRequest.Type.SHORT_PERIOD_REPORT.equals(request.getType())) {

        printService.get().printShortPeriodReport(request.getStart(), request.getEnd());

      }

    } catch (RuntimeException e) {
      log.log(Level.SEVERE, "Printing error!", e);
      return replyWith(PrintResponse.systemError(e));
    } finally {
      log.info("Print service disconnect!");
      printService.get().disconnect();
    }

    return replyWith(PrintResponse.printing());
  }

  private Reply<PrintResponse> replyWith(PrintResponse response) {
    return Reply.with(response).as(GsonTransport.class);
  }

}
