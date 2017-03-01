package com.clouway.pos.print;

import com.google.inject.AbstractModule;
import com.google.sitebricks.SitebricksModule;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class PosPrintingModule extends SitebricksModule {
  @Override
  protected void configureSitebricks() {
    at("/receipt").serve(ReceiptPrintingService.class);
  }


  //    bind(PrintLineHelper.class).to(PrintLineHelperImpl.class).in(Singleton.class);
//    bind(ReceiptPrintService.class).annotatedWith(OfficialReceipt.class).to(OfficialReceiptPrintService.class).in(RequestScoped.class);
//    bind(ReceiptPrintService.class).annotatedWith(FiscalReceipt.class).to(FiscalReceiptPrintService.class).in(RequestScoped.class);



}
