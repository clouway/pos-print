package com.clouway.pos.print.adapter.http;

import com.clouway.pos.print.core.PrinterFactory;
import com.clouway.pos.print.core.ReceiptPrinter;
import com.clouway.pos.print.printer.FP705Printer;
import com.clouway.pos.print.printer.FP705PrinterFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.sitebricks.SitebricksModule;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class HttpModule extends AbstractModule {

  @Override
  protected void configure() {

    final Module pageBricks = new SitebricksModule() {
      @Override
      protected void configureSitebricks() {
        at("/_status").serve(StatusService.class);
        at("/v1/posprint").serve(PrintService.class);
        at("/v1/devices").serve(CashRegistersService.class);
      }
    };

    install(pageBricks);

    bind(PrinterFactory.class).to(FP705PrinterFactory.class);
  }
}