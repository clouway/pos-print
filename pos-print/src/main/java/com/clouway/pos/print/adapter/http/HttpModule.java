package com.clouway.pos.print.adapter.http;

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
        at("/v1/receipts/req/print").serve(PrintService.class);
        at("/v2/receipts").serve(PrintServiceV2.class);
        at("/v1/devices").serve(DeviceConfigurationService.class);
        at("/v1/reports").serve(ReportService.class);
      }
    };

    install(pageBricks);
  }
}