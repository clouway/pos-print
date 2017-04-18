package com.clouway.pos.print.core;

import java.io.IOException;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface ReceiptPrinter {
  
  /**
   * Prints receipt on the device.
   *
   * @param receipt the receipt to be printed
   * @throws IOException in case of failure
   */
  void printReceipt(Receipt receipt) throws IOException;

  /**
   * Prints receipt on the device.
   *
   * @param receipt the receipt to be printed
   * @throws IOException in case of failure
   */
  void printFiscalReceipt(Receipt receipt) throws IOException;

  /**
   * Closes communication with the printer
   *
   * @throws IOException in case of failure
   */
  void close() throws IOException;
}
