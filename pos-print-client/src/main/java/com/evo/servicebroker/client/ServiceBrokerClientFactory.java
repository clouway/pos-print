package com.evo.servicebroker.client;


/**
 * ServiceBrokerClientFactory is the entry point of the
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ServiceBrokerClientFactory {

  public static ServiceBrokerClient getServiceForServer(String serviceAddress) {
    NetworkCommunicator communicator = new NetworkCommunicatorImpl(JsonSerializerBuilder.createSerializer());
    return new RealServiceBrokerClient(serviceAddress,communicator, new ThreadSleeper());
  }
}
