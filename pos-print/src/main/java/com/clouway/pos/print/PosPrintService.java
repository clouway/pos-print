package com.clouway.pos.print;

import com.clouway.pos.print.adapter.http.HttpBackend;
import com.clouway.pos.print.core.CommandCLI;
import com.sampullara.cli.Args;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class PosPrintService {

  public static void main(String[] args) {

    CommandCLI commandCLI = new CommandCLI();

    Args.parse(commandCLI, args);

    HttpBackend backend = new HttpBackend(commandCLI);
    backend.start();
    
    System.out.printf("POS Print Service is up and running on port: %d", commandCLI.httpPort());

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
