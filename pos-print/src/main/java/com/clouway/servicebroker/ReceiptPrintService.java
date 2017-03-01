package com.clouway.servicebroker;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface ReceiptPrintService {
  /**
   * Printing the receipt.
   */
  void printReceipt() throws InterruptedException;
}
