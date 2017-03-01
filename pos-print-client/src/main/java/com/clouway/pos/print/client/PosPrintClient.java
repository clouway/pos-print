package com.clouway.pos.print.client;

/**
 * Client who sends object to server for printing and receive responses.
 * 
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface PosPrintClient {

  /**
   * Schedules receipt to be printed on the cash register.
   * @param receiptId the receipt id that need to be printed
   * @param callbackUrlAddress the url address of the receipt that need to be used for the retrieving of the receipt
   */
  void scheduleReceiptForPrinting(String receiptId, String callbackUrlAddress);
}
