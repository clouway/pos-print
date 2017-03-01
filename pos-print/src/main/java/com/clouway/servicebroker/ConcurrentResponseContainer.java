package com.clouway.servicebroker;

import com.evo.servicebroker.client.PrintResponse;
import com.evo.servicebroker.client.Receipt;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;

import java.util.concurrent.TimeUnit;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
@Singleton
public class ConcurrentResponseContainer implements ResponseContainer {
  private final Cache<String, PrintResponse> responses = CacheBuilder.newBuilder()
          .maximumSize(1000)
          .concurrencyLevel(32)
          .expireAfterWrite(5, TimeUnit.MINUTES)
          .build();

  public void addResponse(Receipt receipt, PrintResponse response) {
    responses.put(receipt.generateUniqueId(), response);
  }

  public PrintResponse removeResponse(Receipt receipt) {

    PrintResponse printResponse = responses.getIfPresent(receipt.generateUniqueId());
    if (printResponse != null) {
      responses.invalidate(receipt.generateUniqueId());
    }
    
    return printResponse;
  }

  public PrintResponse getResponse(Receipt receipt) {
    return responses.getIfPresent(receipt.generateUniqueId());
  }
  
}
