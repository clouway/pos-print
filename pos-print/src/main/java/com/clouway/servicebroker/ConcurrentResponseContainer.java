package com.clouway.servicebroker;

import com.evo.servicebroker.client.PrintResponse;
import com.evo.servicebroker.client.Receipt;
import com.google.common.collect.MapMaker;
import com.google.inject.Singleton;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
@Singleton
public class ConcurrentResponseContainer implements ResponseContainer {
  private ConcurrentMap<String, PrintResponse> responses = new MapMaker()
          .concurrencyLevel(32)
          .expiration(5, TimeUnit.MINUTES)
          .makeMap();

  public void addResponse(Receipt receipt, PrintResponse response) {
    responses.put(receipt.generateUniqueId(), response);
  }

  public PrintResponse removeResponse(Receipt receipt) {
    return responses.remove(receipt.generateUniqueId());
  }

  public PrintResponse getResponse(Receipt receipt) {
    return responses.get(receipt.generateUniqueId());
  }
  
}
