package com.clouway.pos.print.client;


import com.clouway.pos.print.client.internal.NetworkCommunicator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class RealPosPrintClient implements PosPrintClient {

  private final String host;
  private final NetworkCommunicator communicator;

  public RealPosPrintClient(String host, NetworkCommunicator communicator) {
    this.host = host;
    this.communicator = communicator;
  }

  @Override
  public void scheduleReceiptForPrinting(String receiptId, String callbackUrlAddress) {
    try {
      String encodedCallback = URLEncoder.encode(callbackUrlAddress, "UTF-8");
      communicator.get(host + "/receipt/schedule?receiptId=" + receiptId + "&callback=" + encodedCallback);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

  }
}
