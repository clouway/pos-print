package com.clouway.servicebroker.print;

import com.clouway.servicebroker.print.internal.PrinterConnector;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Real implementation to the print service that provides the printing api.
 * 
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class TremolPrintService implements PrintService {
  private final Logger log = Logger.getLogger(TremolPrintService.class.getName());
  private final PrinterConnector connector;
  private final PrinterBase printerBase;

  @Inject
  public TremolPrintService(Provider<PrinterBase> printerBase, PrinterConnector connector) {
    this.printerBase = printerBase.get();
    this.connector = connector;
  }


  public void connect() {
    log.info("Establish printer connection");
    // establish printer connection
    connector.openConnection();

    log.info("Open streams to that connection");
    // now we can open streams to that connection
    printerBase.openStreams();    
  }

  public void disconnect() {
    connector.closeConnection();
  }

  public String getFiscalNumber() throws PrinterErrorException {
    return printerBase.getFiscalNumber();
  }

  public PrinterStatus getStatus() throws PrinterErrorException {
    return printerBase.getStatus();
  }


  public void printText(String text, TextAlign align) {
    printerBase.printText(text, align);
  }

  public void printFiscalText(String text) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void openBon() {
    //TODO: Retrieve user and pass from configuration
    try {
      printerBase.openBon(1, "0");
    } catch(PrinterErrorException e) {
      if (e.containsError(PrinterError.openBon())) {
        try {
          printerBase.closeFiscalBon();
        } catch(Exception ee) {
           // we don't have to handle this exception, cause previous opened
           // bon can be simple bon not a fiscal bon
        }

        printerBase.closeBon();

        printerBase.openBon(1,"0");
      } else {
        throw e;
      }
    }

  }

  public void openFiscalBon() {
    //TODO: Retrieve user and pass from configuration
    try {
      printerBase.openFiscalBon(1, "0", false, false);
    } catch (PrinterErrorException e) {
      if (e.containsError(PrinterError.openFiscalBon())) {
        try{
          printerBase.closeBon();
        } catch(Exception ee) {
           // we don't have to handle this exception, cause previous opened
           // bon can be simple bon not a fiscal bon
        }

        closeFiscalBon();

        printerBase.openFiscalBon(1, "0", false, false);
      } else {
        throw e;
      }
    }

  }

  public void closeBon() {
    printerBase.closeBon();
  }


  public void closeFiscalBon() {

    float sum = printerBase.calcIntermediateSum(false, false, false, 0f, '1');
    log.info("On close fiscal bon sum is: " + sum);
    if (sum >= 0.000001) {
      log.info("Paying sum:" + sum);
      printerBase.payment(sum, 0, false);
    } else {
      log.info("Paying sum: " + 0.00);
      try {
        printerBase.payment(0f, 0, false);
      } catch(Exception e) { }            

    }
    
    printerBase.closeFiscalBon();
  }


  public void lineFeed() {
    printerBase.lineFeed();
  }

  public void sellFree(String name, float price, float quantity, float discount, String department) {
    printerBase.sellFree(name, '1', price, quantity, discount);
  }


  public void printLogo() {
    printerBase.printLogo();
  }

  public void printShortPeriodReport(Date start, Date end) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void printPeriodReport(Date start, Date end) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void printDepartmentDailyReport(boolean clearDailyTurnover) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
