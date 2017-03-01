package com.clouway.servicebroker.print;

import com.clouway.servicebroker.print.internal.PrinterConnection;
import com.clouway.servicebroker.print.internal.PrinterConnector;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;

/**
 * InternalPrintingModule contains all common behaviour that is required by the injector to inject the printing services.
 * 
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class InternalPrintingModule extends AbstractModule {
  
  @Override
  protected void configure() {
//    bind(PrintService.class).to(TremolPrintService.class);
    bind(PrintService.class).to(DatecsPrintService.class).in(RequestScoped.class);
  }

  @Provides
  @RequestScoped
  public PrinterConnection getCurrentConnection(Provider<PrinterConnector> connectorProvider) {
    return connectorProvider.get().getCurrentConnection();
  }
  
  @Provides
  @RequestScoped
  public PrinterBase getPrinterBase(Provider<PrinterConnection> connection) {
    return new RealPrinterBase(connection);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof InternalPrintingModule;
  }

  @Override
  public int hashCode() {
    return InternalPrintingModule.class.hashCode();
  }
}
