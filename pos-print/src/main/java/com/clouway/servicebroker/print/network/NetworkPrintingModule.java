package com.clouway.servicebroker.print.network;

import com.clouway.servicebroker.print.Printer;
import com.clouway.servicebroker.print.PrintingModule;
import com.clouway.servicebroker.print.internal.PrinterConnector;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;

import javax.inject.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class NetworkPrintingModule extends AbstractModule implements PrintingModule{

  private Logger log = Logger.getLogger(NetworkPrintingModule.class.getName());

  @Override
  protected void configure() {
    bind(PrinterConfigurationBase.class).to(InMemoryPrinterConfigurationBase.class);
  }


  @Provides
  @RequestScoped
  public PrinterConnector getPrinterConnector(Provider<PrinterSocket> printerSocket) {
    log.info("Establishing printer connection");
        
    try {
      return new NetworkPrinterConnector(printerSocket.get());
    } catch(Exception e) {
      log.log(Level.INFO, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  @Provides
  @RequestScoped
  public PrinterSocket getPrinterSocket(Provider<NetworkPrinterConfiguration> configuration) {
    log.info("Creating printer socket");
    return new PrinterSocket(configuration.get().getIp(), configuration.get().getPort());
  }

  @Provides
  @RequestScoped
  public NetworkPrinterConfiguration getNetworkConfiguration(PrinterConfigurationBase configuration, Provider<Printer> printer) {
    log.info("Retrieving printer configuration");

    try {
      Printer pr = printer.get();
      log.info("Printer name: " + pr.getName());
      return configuration.getPrinterConfiguration(pr);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }


}
