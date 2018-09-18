package com.clouway.pos.print;

import com.clouway.pos.print.core.BackgroundReceiptPrintingService;
import com.clouway.pos.print.adapter.http.HttpBackend;
import com.clouway.pos.print.adapter.http.HttpModule;
import com.clouway.pos.print.core.CoreModule;
import com.clouway.pos.print.adapter.db.PersistentModule;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
import com.sampullara.cli.Args;

import java.util.List;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class PosPrintService {
  private static final Integer MAX_RETRY_ATTEMPTS = 10;

  public static void main(String[] args) {
    CommandCLI commandCLI = new CommandCLI();
    try {
      Args.parse(commandCLI, args);
    } catch (IllegalArgumentException e) {
      Args.usage(commandCLI);
      System.exit(-1);
      return;
    }

    MongoClient client = establishMongoDbConnection(commandCLI.dbHost());

    Injector injector = Guice.createInjector(
      new CoreModule(),
      new HttpModule(),
      new PersistentModule(client, commandCLI.dbName())
    );


    HttpBackend backend = new HttpBackend(commandCLI.httpPort(), injector);
    backend.start();

    BackgroundReceiptPrintingService backgroundPrinter = injector.getInstance(BackgroundReceiptPrintingService.class);
    backgroundPrinter.startAsync().awaitRunning();

    System.out.printf("POS Print Service is up and running on port: %d\n", commandCLI.httpPort());

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("POS Print Service is going to shutdown.");
      try {
        backend.stop();
        backgroundPrinter.stopAsync();
      } catch (Exception e) {
        System.out.println("Failed to stop server due: " + e.getMessage());
      }
      System.out.println("POS Print Service goes down.");
    }));
  }

  private static MongoClient establishMongoDbConnection(List<String> hosts) {
    List<ServerAddress> replicaSet = Lists.newArrayList();
    for (String s : hosts) {
      replicaSet.add(new ServerAddress(s));
    }

    MongoClientOptions opt = MongoClientOptions
      .builder()
      .serverSelectionTimeout(2000)
      .connectTimeout(300)
      .socketTimeout(1000)
      .maxWaitTime(500)
      .connectionsPerHost(12)
      .build();

    MongoClient client = new MongoClient(replicaSet, opt);

    boolean successFullyConnected = true;
    for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
      try {
        List<ServerAddress> addressList = client.getServerAddressList();
        System.out.println("Got connected to databases: " + addressList);

        successFullyConnected = true;
        break;
      } catch (MongoTimeoutException e) {
        System.out.printf("attempt: [%d] unable to connect to hosts '%s', failed with: %s\n", attempt, hosts, e.getMessage());
        successFullyConnected = false;
      }
    }
    if (!successFullyConnected) {
      System.out.println("could not establish a connection with the database.");
      System.exit(-1);
    }
    return client;
  }
}
