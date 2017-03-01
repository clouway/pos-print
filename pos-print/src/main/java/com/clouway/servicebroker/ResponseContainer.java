package com.clouway.servicebroker;

import com.evo.servicebroker.client.PrintResponse;
import com.evo.servicebroker.client.Receipt;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface ResponseContainer {
  void addResponse(Receipt receipt, PrintResponse response);

  PrintResponse removeResponse(Receipt receipt);
}
