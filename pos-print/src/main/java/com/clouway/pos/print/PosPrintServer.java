package com.clouway.pos.print;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.util.Modules;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class PosPrintServer {
  private Server server;

  public Injector startServer(Integer port, Module[] overrideModules) {

    server = new Server(port);

    Context root = new Context(server, "/", Context.SESSIONS);

    root.addFilter(GuiceFilter.class, "/*", 0);
    root.addServlet(DefaultServlet.class, "/");

    try {
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

    Module appModule;
    if (overrideModules.length != 0) {
      appModule = Modules.override(new PosPrintingModule()).with(overrideModules);
    } else {
      appModule = new PosPrintingModule();
    }
    Injector injector = Guice.createInjector(appModule);
    return injector;
  }

  public void stopServer() {
    try {
      server.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
