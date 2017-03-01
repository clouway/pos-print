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

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.printf("POS Print Service is going to shutdown.");
      try {
        backend.stop();
      } catch (Exception e) {
        System.out.println("Failed to stop server due: " + e.getMessage());
      }
      System.out.printf("POS Print Service goes down.");
    }));
  }
}
