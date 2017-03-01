package com.clouway.servicebroker;

/**
 * Monitor each printer and keep track on receipts that need to be printed on given printer.
 * Put all receipts for given printer in queue. Receipts in queue is not printed from here.
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface PrinterMonitor {
  /**
   * Add receipt in queue for printer.
   */
  void addReceipt();

  /**
   * Check if receipt is next for printing or its not. Also check how much time receipt wait for printing. If receipt
   * wait too long for printing throws {@link ReceiptExpireException}. Receipt remains in queue
   * until someone call releasePrinter() method.
   *
   * @return true if receipt is next for printing and false otherwise.
   */
  Boolean isMyTurn();

  /**
   * Remove first receipt for specified printer from queue.
   */
  void releasePrinter();
}
