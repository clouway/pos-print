package com.clouway;

import com.clouway.pos.print.client.Receipt;

/**
* @author Miroslav Genov (mgenov@gmail.com)
*/
public class FakeTelcoNGServer {

  private final Integer port;

  public FakeTelcoNGServer(Integer port) {
    this.port = port;
  }

  public void startServer() {

  }

  public void stopServer() {

  }

  public String getReceiptRetrieveCallbackUrl(String receiptId) {
    return "http://localhost:" + port + "/r/receipt/" + receiptId;
  }

  public void pretendReceiptExists(Receipt receipt) {

  }
}
