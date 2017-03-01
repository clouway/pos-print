package com.clouway.pos.print.client;


import com.clouway.pos.print.client.internal.NetworkCommunicatorImpl;

/**
 * PosPrintClientFactory is the entry point of the
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class PosPrintClientFactory {

  public static PosPrintClient getServiceForServer(String serviceAddress) {
    return new RealPosPrintClient(serviceAddress,new NetworkCommunicatorImpl(JsonSerializerFactory.createSerializer()));
  }
}
