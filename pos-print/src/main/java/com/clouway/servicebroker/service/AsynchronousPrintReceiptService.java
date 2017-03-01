package com.clouway.servicebroker.service;

import com.clouway.common.AllowedAddress;
import com.clouway.servicebroker.ReceiptContainer;
import com.clouway.servicebroker.ResponseContainer;
import com.clouway.servicebroker.ServerPort;
import com.evo.servicebroker.client.JsonSerializer;
import com.evo.servicebroker.client.PrintResponse;
import com.evo.servicebroker.client.PrintingState;
import com.evo.servicebroker.client.Receipt;
import com.evo.servicebroker.client.ResponseInfo;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;
import org.mortbay.io.Buffer;
import org.mortbay.jetty.client.ContentExchange;
import org.mortbay.jetty.client.HttpClient;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
@At("/printReceipt")
@Service
public class AsynchronousPrintReceiptService {
  private final Logger log = Logger.getLogger(AsynchronousPrintReceiptService.class.getName());
  private static final String LOCAL_PRINT_ADDRESS = "/realPrintReceipt";
  private static final String METHOD = "POST";
  private final Set<String> allowedAddress;
  private final ReceiptContainer container;
  private final Provider<Integer> port;
  private final Provider<Receipt> receiptProvider;
  private final JsonSerializer serializer;
  private final ResponseContainer responseContainer;
  private final Provider<HttpClient> client;

  @Inject
  public AsynchronousPrintReceiptService(@AllowedAddress Set<String> allowedAddress,
                                         ReceiptContainer container,
                                         @ServerPort Provider<Integer> port,
                                         Provider<Receipt> receiptProvider,
                                         JsonSerializer serializer,
                                         ResponseContainer responseContainer, Provider<HttpClient> client) {
    this.allowedAddress = allowedAddress;
    this.container = container;
    this.port = port;
    this.receiptProvider = receiptProvider;
    this.serializer = serializer;
    this.responseContainer = responseContainer;
    this.client = client;
  }

  @Get
  @Post
  public Reply<PrintResponse> printReceipt() {
    log.info("-----------------------------------------------Async receipt for printing received.-------------------------------------------------------------");
    PrintResponse response = PrintResponse.printing();

    // get current receipt
    Receipt receipt = receiptProvider.get();

    log.info("@*@*@ Receipt ip: " + receipt.getPrintingIp() + " Cashier name: " + receipt.getCashierName());

    // check if is valid client
    boolean validClient = isValidClientAddress(receipt.getPrintingIp());

    // if client is not valid return response with not allowed message
    // maybe its better to return only string instead serialized object?
    if (!validClient) {
      return Reply.with(PrintResponse.notAllowedRequest()).as(GsonTransport.class);
    }

    try {

      // if receipt is not in queue, add receipt and start printing
      // else return message that receipt is printing in the moment.
      // can be extended, we can have states on what receipt is waiting for printing, is printing in moment or print is finished.
      if (!receiptWasScheduledForPrinting(receipt)) {

        log.info("Receipt is not in queue. Adding receipt in queue");

        // add receipt in container
        container.addReceipt(receipt);

        // send receipt for printing to print servlet
        handlePrinting(receipt);

      } else {

        log.info("Check printing receipt state. Receipts in queue: " + container.getSize());

        Receipt mappedReceipt = container.getReceipt(receipt);
        PrintingState state = PrintingState.from(mappedReceipt.getPrintingState());

        if (state == null) {

          log.info("Receipt state is null!");

        } else if (state.equals(PrintingState.PRINTED)) {

          log.info("@@@@@ Receipt is printed. Return success response, and remove receipt from container. @@@@");

          container.removeReceipt(mappedReceipt);
          response = PrintResponse.success();

        } else if (state.equals(PrintingState.PRINTING)) {

          log.info("Receipt is printing at the moment.");

          response = PrintResponse.printing();
        } else if (state.equals(PrintingState.ERROR)) {

          log.info("Printing on this receipt result with error. Remove receipt from container.");

          container.removeReceipt(mappedReceipt);

          log.info("Return print response exception with errors.");

          response = responseContainer.removeResponse(mappedReceipt);

        } else {

          log.info("Receipt is still in queue and wait for printing.");
          response = PrintResponse.inQueue();

        }
      }

    } catch (ServletException e) {

      log.info("Received servlet exception: " + e.getMessage());
      // return system error response, the error contains message from exception.
      response = PrintResponse.systemError(e);

    } catch (IOException e) {

      log.info("Catch IOException with message: " + e.getMessage());
      response = PrintResponse.systemError(e);
    }

    return Reply.with(response).as(GsonTransport.class);
  }

  private boolean receiptWasScheduledForPrinting(Receipt receipt) {
    return container.containsReceipt(receipt);
  }

  private void handlePrinting(final Receipt receipt) throws ServletException, IOException {

    // when return response mark receipt as printed in the container
    ContentExchange exchange = new ContentExchange(true) {
      @Override
      protected void onResponseContent(Buffer content) throws IOException {
        super.onResponseContent(content);

        String responseContent = getResponseContent();
        PrintResponse response = (PrintResponse) serializer.deserializeEntities(responseContent, PrintResponse.class);

        // if receipt is printed success.
        if (response.isSuccess()) {

          log.info("Printing on receipt success");

          // mark as printed in container
          changeReceiptState(receipt, PrintingState.PRINTED);
        } else if (response.isErrorResponse()) {

          log.info("Error printing response received: ");
          for (ResponseInfo info : response.getInfo()) {

            log.info("*** error info messages: " + info.getInfoMessage());

          }

          responseContainer.addResponse(receipt, response);

          // mark that have error while printing
          changeReceiptState(receipt, PrintingState.ERROR);
        }
      }
    };

    String url = "http://localhost:" + port.get() + LOCAL_PRINT_ADDRESS + "?receiptKey=" + receipt.generateUniqueId();
    exchange.setURL(url);
    exchange.setMethod(METHOD);

    // start the exchange
    log.info("Send receipt for printing to printing servlet.");
    client.get().send(exchange);
  }

  private void changeReceiptState(Receipt receipt, PrintingState state){
    Receipt mappedReceipt = container.getReceipt(receipt);
    Receipt stateReceipt = Receipt.withIsPrintedState(mappedReceipt, state);
    container.addReceipt(stateReceipt);
  }

  // this need (can) to be moved to some filter

  private boolean isValidClientAddress(String printingIp) {

    log.info("Receive print receipt request from: " + printingIp);

    if (allowedAddress.contains(printingIp)) {
      log.info("Valid client address.");
      return true;
    }

    log.info("Not valid client address.");
    return false;
  }
}
