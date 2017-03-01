package com.clouway.pos.print;

import com.clouway.pos.print.client.Receipt;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface ReceiptPrintService {
  /**
   * Prints the provided receipt.
   */
  void printReceipt(Receipt receipt);
}
