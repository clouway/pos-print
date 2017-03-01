package com.evo.servicebroker.client;

/**
 * Client who sends object to server for printing and receive responses.
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface ServiceBrokerClient {

  /**
   * Try to print receipt. Receipt is send for printing all time until is not received response for successful printed.
   * If Response with exception is returned stops trying to print receipt and return response.
   *
   * @param t     object to be printed.
   * @param clazz object class.
   * @return {@link com.evo.servicebroker.client.PrintResponse} received response.
   */
  <T> PrintResponse print(T t, Class<T> clazz);

  /**
   * Prints  the requested daily financial report
   *
   * @param request the request that is representing the type of the report that need to be printed
   */
  void printFinancialReport(FinancialReportRequest request);
}
