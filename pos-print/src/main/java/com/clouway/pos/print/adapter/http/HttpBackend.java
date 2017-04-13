package com.clouway.pos.print.adapter.http;

import com.clouway.pos.print.core.CommandCLI;
import com.clouway.pos.print.persistent.PersistentCashRegisterRepository;
import com.clouway.pos.print.persistent.PersistentModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * HttpBackend is a HTTP backend server which listens for HTTP requests and dispatches them
 * to proper handlers.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class HttpBackend {
  private final Server server;
  private final CommandCLI commandCLI;

  public HttpBackend(CommandCLI commandCLI) {
    this.commandCLI = commandCLI;
    this.server = new Server(commandCLI.httpPort());
  }

  public void start() {
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");

    MongoClientOptions opt = MongoClientOptions
      .builder()
      .serverSelectionTimeout(5000)
      .connectTimeout(300)
      .socketTimeout(1000)
      .maxWaitTime(500)
      .connectionsPerHost(3)
      .build();

    MongoClient client = new MongoClient(commandCLI.dbHost(), opt);

    /*
     * Guice Servlet Handler
     */
    context.addServlet(DefaultServlet.class, "/");
    context.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE));
    context.addEventListener(new GuiceServletContextListener() {
      @Override
      protected Injector getInjector() {
        return Guice.createInjector(new HttpModule(), new PersistentModule(client,commandCLI.dbName()));
      }
    });

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{context});

    server.setHandler(handlers);
    try {
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void stop() throws Exception {
    server.stop();
  }

}
