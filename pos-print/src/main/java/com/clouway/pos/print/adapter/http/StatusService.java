package com.clouway.pos.print.adapter.http;

import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
@Service
@At("/_status")
public class StatusService {
  
  @Get
  public Reply<?> getStats() {
    return Reply.with("OK");
  }
}
