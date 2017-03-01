package com.evo.servicebroker.client;

import java.util.Set;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ServiceBrokerErrors {
  public static ServiceBrokerErrors with(Set<String> messages) {
    ServiceBrokerErrors serviceBrokerErrors = new ServiceBrokerErrors();
    serviceBrokerErrors.messages = messages;
    return serviceBrokerErrors;
  }

  private Set<String> messages;

  private ServiceBrokerErrors() {
  }

  public Set<String> getMessages() {
    return messages;
  }
}
