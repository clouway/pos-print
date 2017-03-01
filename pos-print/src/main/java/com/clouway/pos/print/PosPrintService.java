package com.clouway.pos.print;

import com.clouway.pos.print.adapter.http.HttpBackend;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class PosPrintService {

  public static void main(String[] args) {

    final int httpPort = 8080;

    HttpBackend backend = new HttpBackend(httpPort);
    backend.start();
    
    System.out.printf("POS Print Service is up and running on port: %d", httpPort);
  }
}
