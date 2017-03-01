package com.clouway.servicebroker;

import com.clouway.servicebroker.print.PrintingService;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * ServiceBrokerApp is the entry point of the application.
 * 
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class ServiceBrokerApp {
  
  /**
   * Starts the ServiceBroker's application server.
   * @param args 
   */
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ServiceBrokerModule(), PrintingService.usingNetworkConnection().buildModule());
    ServiceBroker serviceBroker = injector.getInstance(ServiceBroker.class);
    serviceBroker.startServer();
    serviceBroker.join();
  }

}
