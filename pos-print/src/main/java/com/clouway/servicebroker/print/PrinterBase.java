package com.clouway.servicebroker.print;

/**
 * PrinterBase represents the base functionality that is provided by the printer. With <code>PrinterBase</code> you are free to send different
 * kind of communication to an external printer. 
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
interface PrinterBase {
    
  void openStreams(); 

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
   * Makes payment of the current note.
   * @param sum
   * @param type
   * @param noRest
   * @throws PrinterErrorException is thrown in case payment cannot be made due some communication error with the printer
   */
  void payment(float sum, int type, boolean noRest) throws PrinterErrorException;


  /**
   * Prints text to the printer by providing alignment of the text.
   * @param text the text to be printed
   * @param align the text's alignment
   */
  void printText(String text, TextAlign align);


  /**
   * Opens a new bon for printing.
   * @param oper the operator identifier
   * @param pass operator's password
   */
  void openBon(int oper, String pass);

  /**
   * Closes the current bon. 
   */
  void closeBon();

  /**
   * Prints empty line.
   */
  void lineFeed();


 /**
   * Calculates the sub total sum of the receipt
   *
   * @param print     flag for print the sub total sum
   * @param show      flag for show the sub total sum on the external display
   * @param isPercent flag for percentage discount/addition
   * @param discount  discount/addition value
   * @param taxgrp    specifies the tax group - ignored in Bulgarian FP version
   * @return returns the sub total sum
   * @throws ZFPException if the input parameters are incorrect or in case of communication error
   */
  float calcIntermediateSum(boolean print, boolean show, boolean isPercent,
                                   float discount, char taxgrp);
  
  /**
   * 
   * @param name
   * @param taxgrp
   * @param price
   * @param quantity
   * @param discount
   */
  void sellFree(String name, char taxgrp, float price, float quantity, float discount);


  /**
   * Opens a new fiscal bon.
   * @param oper the operator identfier
   * @param pass opeator's password
   * @param detailed detailed bon or not
   * @param vat fiscal bon with vat or not
   */
  void openFiscalBon(int oper, String pass, boolean detailed, boolean vat);

  /**
   * Closes the current fiscal bon.
   */
   void closeFiscalBon();

  void printLogo();
}
