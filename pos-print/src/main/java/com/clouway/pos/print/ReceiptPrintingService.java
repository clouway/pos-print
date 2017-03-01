package com.clouway.pos.print;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
@At("/receipt")
@Service
public class ReceiptPrintingService {

//  private final Provider<HttpServletRequest> requestProvider;

  @Inject
  public ReceiptPrintingService() {

  }

  @Get
  @At("/schedule")
  public Reply<?> schedule() {
    System.out.println("OK");
    return Reply.saying().ok();
  }

}
