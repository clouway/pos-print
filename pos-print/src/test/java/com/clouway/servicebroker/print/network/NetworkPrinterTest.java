package com.clouway.servicebroker.print.network;

import com.clouway.servicebroker.print.CommunicationErrorException;
import com.clouway.servicebroker.print.Printer;
import com.clouway.servicebroker.print.PrinterCommunicationError;
import com.clouway.servicebroker.print.PrintingService;
import com.clouway.servicebroker.print.internal.PrinterConnector;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import com.google.inject.util.Modules;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
@RunWith(JMock.class)
public class NetworkPrinterTest {
  private Mockery context = new JUnit4Mockery();

  private PrinterConnector printerConnector;

  @Before
  public void initialize() {
    printerConnector = context.mock(PrinterConnector.class);
  }

  @Test
  public void setupNetworkingPrintingModule() {
    Module printingModule = PrintingService.usingNetworkConnection().buildModule();
    assertNotNull("hm, no printing module was installed?", printingModule);
  }

  @Test
  public void handlePrinterConnectionError() {
    Module module = Modules.override(new NetworkPrintingModule(), new ServletModule()).with(new AbstractModule() {
      @Override
      protected void configure() {
      }

      @Provides
      public Printer newPrinter() {
        return Printer.with("192.168.0.1");
      }

      @Provides
      @Network
      public PrinterSocket getPrinterConnection() {
        return new PrinterSocket("", 1024) {
          @Override
          public PrinterSocket connect() {
            throw new CommunicationErrorException(PrinterCommunicationError.brokenCommunication());
          }
        };
      }

      @Provides
      public NetworkPrinterConfiguration getConfiguration() {
        return new NetworkPrinterConfiguration("192.168.0.1", 1024);
      }

      @Provides
      public PrinterConnector getConnector() {
        return printerConnector;
      }


    });
    Injector injector = Guice.createInjector(module);

    LocalPrinterService service = injector.getInstance(LocalPrinterService.class);
    try {
      service.doWork();
      fail("Exception must be thrown.");
    } catch (CommunicationErrorException ex) {
      assertThat("hm, different error was thrown?", ex.getCommunicationError(), is(PrinterCommunicationError.brokenCommunication()));
    }
  }

  static class LocalPrinterService {
    private final Provider<PrinterSocket> network;

    @Inject
    public LocalPrinterService(@Network Provider<PrinterSocket> network) {
      this.network = network;
    }

    public void doWork() {
      network.get().connect();
    }
  }

}
