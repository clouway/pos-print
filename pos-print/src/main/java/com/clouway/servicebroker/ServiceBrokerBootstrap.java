package com.clouway.servicebroker;

import com.clouway.servicebroker.print.PrintingService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class ServiceBrokerBootstrap extends GuiceServletContextListener {
  private final Logger log = LoggerFactory.getLogger(ServiceBrokerBootstrap.class);

  @Override
  protected Injector getInjector() {
    log.debug("Creating service broker injector.");
    return Guice.createInjector(new ServiceBrokerModule(), PrintingService.usingNetworkConnection().buildModule());
}
}
