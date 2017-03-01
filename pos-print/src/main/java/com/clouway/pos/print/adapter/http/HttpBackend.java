package com.clouway.pos.print.adapter.http;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
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

  public HttpBackend(int port) {
    this.server = new Server(port);
  }

  public void start() {
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");

    /*
     * Guice Servlet Handler
     */
    context.addServlet(DefaultServlet.class, "/");
    context.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE));
    context.addEventListener(new GuiceServletContextListener() {
      @Override
      protected Injector getInjector() {
        return Guice.createInjector(new HttpModule());
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
