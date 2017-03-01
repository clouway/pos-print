package com.clouway.pos.print.adapter.http;

import com.google.sitebricks.SitebricksModule;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class HttpModule extends SitebricksModule {

  @Override
  protected void configureSitebricks() {
    at("/_status").serve(StatusService.class);
  }
}
