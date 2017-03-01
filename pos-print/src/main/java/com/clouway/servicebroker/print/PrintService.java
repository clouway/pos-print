package com.clouway.servicebroker.print;

import java.util.Date;

/**
 * PrintService is the main service provider of the PrintingModule. The {@link com.clouway.servicebroker.print.PrintService} provides the system
 * with ability to print bons, fiscal bons and simple text. Here are some example usages that could be performed by the {@link com.clouway.servicebroker.print.PrintService}:
 *
 * <h4>Connecting to the printer device.</h4>
 * <pre>
 *    PrinterService printService;
 *    ....
 *    printService.connect();
 *
 *    printService.disconnect();
 * </pre>
 *
 * Please note that {@link com.clouway.servicebroker.print.CommunicationErrorException} could be thrown in case the service cannot establish connection
 * to the printer device.
 *
 * <p/>
 *
 * <h4>Printing text message</h4>
 *
 * <pre>
 *    PrinterService printService;
 *    ....
 *    printService.connect();
 *
 *     // left aligned text
 *    printService.printText("Left Aligned",TextAlign.LEFT);
 *   // right aligned text
 *    printService.printText("Right Aligned",TextAlign.RIGHT);
 *
 *    printService.disconnect();
 *
 * </pre>
 *
 *
 * <h4>Printing Fiscal Bon</h4>
 * <pre>
 *    PrinterService printService;
 *    ....
 *    printService.connect();
 *
 *    printService.openFiscalBon();
 *    // we are printing "MyArticle1" using tax group '1', with price of 24.90, quantity 1 with no discount.
 *    printService.sellFree("MyArticle1",'1',24.90f,1f,,0.0f);
 *
 *    printService.closeFiscalBon();
 *
 *    printService.disconnect();
 *
 * <pre>
 *
 * When you are working with fiscal bon the service have to be sure that there is no already existing opened bon. To may handle this situations the
 * service automatically is calculating the summary value of the last bon and closes it after that opens a new fiscal bon. This is happening automatically
 * in the openFiscalBon method.
 *
 * //TODO {mgenov}}: Printing bons
 * 
 * <h4>Error Handling</h4>
 * Printing operation and also opening and closing bons and fiscal bons are throwing {@link com.clouway.servicebroker.print.PrinterErrorException} which may contain
 * different {@link com.clouway.servicebroker.print.PrinterError} that indicates the error kind.
 * <br/>
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public interface PrintService {

  /**
   *  Connects to the target printer device.
   */
  void connect();

  /**
   * Disconnects from the target printer device.
   */
  void disconnect();
  
 /**
   * Gets the current fiscal number that is printing.
   *
   * @return the fiscal number of the printer.
   */
  String getFiscalNumber() throws PrinterErrorException;

  /**
   * Gets printer status information.
   * @return the status of the printer
   */
  PrinterStatus getStatus() throws PrinterErrorException;

  /**
   * Prints text to the printer by providing alignment of the text.
   * @param text the text to be printed
   * @param align the text's alignment
   */
  void printText(String text, TextAlign align);


 /**
   * Prints text to the printer  when fiscal bon is opened
   * @param text the text to be printed
   */
  void printFiscalText(String text);


  /**
   * Opens a new bon for printing.
   */
  void openBon();

  /**
   * Closes the current bon.
   */
  void closeBon();

  /**
   * Prints empty line.
   */
  void lineFeed();

  /**
   *  Sells the provided article. This method should be used only when fiscal bon is printed .
   * @param name the name of the article
   * @param price the price of the article
   * @param quantity the quantity
   * @param discount the discount
   * @param department
   */
  void sellFree(String name, float price, float quantity, float discount, String department);


  /**
   * Opens a new fiscal bon.
   *
   */
  void openFiscalBon();

  /**
   * Closes the current fiscal bon.
   */
   void closeFiscalBon();

  /**
   * Prints specified logo
   */
  void printLogo();

  /**
   * print the daily financial report with or without clearing of daily turnover
   * @param clearDailyTurnover
   */
  void printDepartmentDailyReport(boolean clearDailyTurnover);

  /**
   * Prints  DETAILED SUMS ACCUMULATED IN THE FISCAL MEMORY FOR A GIVEN
   * PERIOD OF TIME
   *
   * @param start
   * @param end
   */
  void printShortPeriodReport(Date start, Date end);

  /**
   * Prints SUMS ACCUMULATED IN THE FISCAL MEMORY FOR A GIVEN
   * PERIOD OF TIME
   *
   * @param start
   * @param end
   */
  void printPeriodReport(Date start, Date end);
}
