package com.evo.servicebroker.client;


import java.util.logging.Logger;


/**
 * Client who sends object to server for printing and receive responses.
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class RealServiceBrokerClient implements ServiceBrokerClient {
  private static final int SLEEP_TIME = 2000;

  private Logger log = Logger.getLogger(RealServiceBrokerClient.class.getName());
  private String serverUrl;
  private NetworkCommunicator communicator;
  private Sleeper sleeper;

  public RealServiceBrokerClient(String serverUrl, NetworkCommunicator communicator, Sleeper sleeper) {
    this.serverUrl = serverUrl;
    this.communicator = communicator;
    this.sleeper = sleeper;
  }


  /**
   * Try to print receipt. Receipt is send for printing all time until is not received response for successful printed.
   * If Response with exception is returned stops trying to print receipt and return response.
   *
   * @param t     object to be printed.
   * @param clazz object class.
   * @return {@link com.evo.servicebroker.client.PrintResponse} received response.
   */
  public <T> PrintResponse print(T t, Class<T> clazz) {
    PrintResponse printResponse;
    String address = serverUrl + "/printReceipt";

    boolean success;

    do {

      log.info("Sending to printer on address: " + address);

      // send request
      printResponse = (PrintResponse) communicator.sendPostRequest(address, t, clazz);

      // if response is for some exception throw new exception
      if (printResponse.isErrorResponse()) {
        log.info("Exception response received. Error message: " + printResponse.getInfo().iterator().next().getInfoMessage());
        break;
      }

      // check if receipt is printed or not
      success = printResponse.isSuccess();

      // if receipt is not printed yet, sleep
      if (!success) {
        log.info("Receipt state is: " + printResponse.getInfo().iterator().next().getInfoMessage());
        sleeper.sleep(SLEEP_TIME);
      }

      // send printing requests all time until receipt is not printed
    } while (!success);

    log.info("Info Message: " + printResponse.getInfo().iterator().next().getInfoMessage());

    // when receipt is printed and everything is fine return response.
    return printResponse;
  }

  public void printFinancialReport(FinancialReportRequest request) {
    PrintResponse printResponse;
    String address = serverUrl + "/printFinancialReport";
    // send request
    printResponse = (PrintResponse) communicator.sendPostRequest(address, request, FinancialReportRequest.class);

    // if response is for some exception throw new exception
    if (printResponse.isErrorResponse()) {
      String errMessage = "";
      if (printResponse.getInfo().iterator().hasNext()) {
        errMessage = printResponse.getInfo().iterator().next().getInfoMessage();
      }
      log.info("Exception response received. Error message: " + errMessage);
      throw new FinancialReportPrintingException(errMessage);
    }

    // if receipt is not printed yet, sleep
    if (printResponse.isSuccess()) {
      log.info("DailyFinancialReportRequest state is: " + printResponse.getInfo().iterator().next().getInfoMessage());
    }
  }

}
