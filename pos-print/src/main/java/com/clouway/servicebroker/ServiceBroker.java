package com.clouway.servicebroker;

import com.google.inject.Inject;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ServiceBroker {
  private final Logger log = LoggerFactory.getLogger(ServiceBroker.class);
  private Server server;
  private Integer port;


  @Inject
  public ServiceBroker(@ServerPort Integer port) {
    this.port = port;
  }

  /**
   * Start server on port
   *
   */
  public void startServer() {
    server = new Server(port);

    Context root = new WebAppContext("web","/");
    server.addHandler(root);

    try {
      server.start();
      log.info("Service-Broker server was started on port: " + port);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Stop server.
   */
  public void stopServer() {
    try {
      server.stop();
      log.info("Service-Broker server is stopped.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void join() {
    try {
      server.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
