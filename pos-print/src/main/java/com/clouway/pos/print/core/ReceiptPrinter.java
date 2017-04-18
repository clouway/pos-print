package com.clouway.pos.print.core;

import java.io.IOException;
import java.time.LocalDateTime;

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
  PrintReceiptResponse printReceipt(Receipt receipt) throws IOException;

  /**
   * Prints receipt on the device.
   *
   * @param receipt the receipt to be printed
   * @throws IOException in case of failure
   */
  PrintReceiptResponse printFiscalReceipt(Receipt receipt) throws IOException;

  /**
   * Gets report for a given operator by providing options for the preferred register
   * state after the operation.
   *
   * @param operatorId the id of the operator for which report need to be issued
   * @param state      the preferred state after operation completes
   * @throws IOException is thrown in case of IO error
   */
  void reportForOperator(String operatorId, RegisterState state) throws IOException;

  /**
   * Gets report for a given time period by providing the starting and end date
   * for the report
   *
   * @param start the starting date for the report
   * @param end   the ending date for the report
   * @param periodType the type of period - SHORT or EXTENDED
   * @throws IOException is thrown in case of IO error
   */
  void reportForPeriod(LocalDateTime start, LocalDateTime end, PeriodType periodType) throws IOException;

  /**
   * Closes communication with the printer
   *
   * @throws IOException in case of failure
   */
  void close() throws IOException;
}