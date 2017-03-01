package com.clouway.pos.print.printer.tremol;


import com.clouway.pos.print.printer.PrintService;
import com.clouway.pos.print.printer.PrinterConnection;
import com.clouway.pos.print.printer.PrinterConnector;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;

/**
 * TremolPrintingModule contains all common behaviour that is required by the injector to inject the printing services.
 * 
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class TremolPrintingModule extends AbstractModule {
  
  @Override
  protected void configure() {
    bind(PrintService.class).to(TremolPrintService.class);
  }

  @Provides
  @RequestScoped
  public PrinterConnection getCurrentConnection(Provider<PrinterConnector> connectorProvider) {
    return connectorProvider.get().getCurrentConnection();
  }
  
  @Provides
  @RequestScoped
  public PrinterBase getPrinterBase(Provider<PrinterConnection> connection) {
    return new TremolPrinterBase(connection);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof TremolPrintingModule;
  }

  @Override
  public int hashCode() {
    return TremolPrintingModule.class.hashCode();
  }
}
