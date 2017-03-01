package com.clouway.servicebroker.service;

import com.clouway.servicebroker.PrinterMonitor;
import com.clouway.servicebroker.ReceiptExpireException;
import com.clouway.servicebroker.ReceiptPrintService;
import com.evo.servicebroker.client.PrintResponse;
import com.evo.servicebroker.client.ResponseInfo;
import com.clouway.servicebroker.print.CommunicationErrorException;
import com.clouway.servicebroker.print.PrinterCommunicationError;
import com.clouway.servicebroker.print.PrinterError;
import com.clouway.servicebroker.print.PrinterErrorException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
@At("/realPrintReceipt")
@Service
public class PrintReceiptService {
  private final Logger log = Logger.getLogger(PrintReceiptService.class.getName());
  private ReceiptPrintService printService;
  private Provider<HttpServletRequest> request;
  private PrinterMonitor printerMonitor;

  @Inject
  public PrintReceiptService(ReceiptPrintService printService,
                             Provider<HttpServletRequest> request,
                             PrinterMonitor printerMonitor) {
    this.printService = printService;
    this.request = request;
    this.printerMonitor = printerMonitor;
  }

  @Post
  @Get
  public Reply<PrintResponse> proceed() {
    log.info("Request for printing receipt received!");

    PrintResponse response;
    try {

      printerMonitor.addReceipt();

      while (printerMonitor.isMyTurn().equals(false)) {
        log.info("Its not my turn. Wait.");
        Thread.sleep(2000);
      }

      String receiptKey = request.get().getParameter("receiptKey");

      log.info("Start printing. Receipt with key: " + receiptKey);

      // print receipt with key
      printService.printReceipt();

      response = PrintResponse.success();

    } catch (CommunicationErrorException e) {
      log.log(Level.SEVERE, e.getMessage(), e);

      PrinterCommunicationError printerError = e.getCommunicationError();
      ResponseInfo error = ResponseInfo.with(printerError.getErrorMessage());

      response = PrintResponse.withExceptionMessages(error);

    } catch (PrinterErrorException e) {
      log.info("Printer error exception catched!");

      Set<ResponseInfo> errors = new HashSet<ResponseInfo>();
      for (PrinterError printerErrors : e.getErrors()) {
        log.info("*** printer error catched : "+printerErrors.getErrorMessage());
        ResponseInfo responseError = ResponseInfo.with(printerErrors.getErrorMessage());
        errors.add(responseError);
      }

      response = PrintResponse.withExceptionMessages(errors);

    } catch (ReceiptExpireException e) {

      log.info("Receipt expire exceptionreceived. " + e.getMessage());

      response = PrintResponse.receiptExpire();

    } catch (Exception e) {

      log.log(Level.SEVERE, e.getMessage(), e);

      ResponseInfo error = ResponseInfo.with(e.getMessage());
      response = PrintResponse.withExceptionMessages(error);

    } finally {
      releasePrinter();
    }

    return Reply.with(response).as(GsonTransport.class);
  }

  private void releasePrinter() {
    log.info("Release printer");
    printerMonitor.releasePrinter();
  }

}
