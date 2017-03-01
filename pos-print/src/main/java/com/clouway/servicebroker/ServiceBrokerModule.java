package com.clouway.servicebroker;

import com.clouway.common.CommonModule;
import com.clouway.servicebroker.async.AsyncDispatchingFilter;
import com.clouway.servicebroker.service.PrintFiscalReportService;
import com.evo.servicebroker.client.FinancialReportRequest;
import com.evo.servicebroker.client.JsonSerializer;
import com.evo.servicebroker.client.JsonSerializerBuilder;
import com.evo.servicebroker.client.Receipt;
import com.evo.servicebroker.client.ReceiptType;
import com.clouway.servicebroker.print.Printer;
import com.clouway.servicebroker.service.AsynchronousPrintReceiptService;
import com.clouway.servicebroker.service.PrintReceiptService;
import com.clouway.servicebroker.service.TestPageService;
import com.google.common.io.ByteStreams;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.google.sitebricks.SitebricksModule;
import org.mortbay.jetty.client.HttpClient;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ServiceBrokerModule extends AbstractModule {
  private final Logger log = Logger.getLogger(ServiceBrokerModule.class.getName());

  @Override
  protected void configure() {

    final Module servletConfiguration = new ServletModule() {
      @Override
      protected void configureServlets() {
        filter("/*").through(AsyncDispatchingFilter.class);
      }
    };

    final Module pages = new SitebricksModule() {
      @Override
      protected void configureSitebricks() {
        at("/printReceipt").serve(AsynchronousPrintReceiptService.class);
        at("/realPrintReceipt").serve(PrintReceiptService.class);
        at("/test").serve(TestPageService.class);
        at("/printFinancialReport").serve(PrintFiscalReportService.class);
      }
    };

    bind(PrintLineHelper.class).to(PrintLineHelperImpl.class).in(Singleton.class);
    bind(ReceiptPrintService.class).annotatedWith(OfficialReceipt.class).to(OfficialReceiptPrintService.class).in(RequestScoped.class);
    bind(ReceiptPrintService.class).annotatedWith(FiscalReceipt.class).to(FiscalReceiptPrintService.class).in(RequestScoped.class);
    bind(ReceiptContainer.class).to(ConcurrentReceiptContainer.class);
    bind(PrinterMonitor.class).to(QueuePrinterMonitor.class);
    bind(ResponseContainer.class).to(ConcurrentResponseContainer.class);
    install(servletConfiguration);
    install(pages);
    install(new CommonModule());
  }

  @Provides
  @Singleton
  public HttpClient getHttpClient() {
    HttpClient client = new HttpClient();
    client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
    try {
      client.start();

    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
    return client;
  }

  @Provides
  public JsonSerializer getJsonSerializer() {
    return JsonSerializerBuilder.createSerializer();
  }

  @Provides
  @RequestScoped
  public Printer getPrinter(Provider<Receipt> receipt, Provider<FinancialReportRequest> reportRequest) {
    if (receipt.get() == null) {
      return Printer.with(reportRequest.get().getRemoteIpAddress());
    }
    return Printer.with(receipt.get().getPrintingIp());
  }


  @Provides
  @RequestScoped
  public FinancialReportRequest getFinancialReportRequest(Provider<JsonSerializer> jsonSerializer, Provider<HttpServletRequest> requestProvider, ReceiptContainer container) {
    HttpServletRequest request = requestProvider.get();

    // get item from JSON
    FinancialReportRequest reportRequest;
    try {

      log.info("Parsing receipt");
      byte[] stream = ByteStreams.toByteArray(request.getInputStream());

      String json = new String(stream, "UTF-8");

      System.out.println("JSON: " + json);

      Object deserialized = jsonSerializer.get().deserializeEntities(json, FinancialReportRequest.class);
      reportRequest = (FinancialReportRequest) deserialized;
    } catch (IOException e) {
      log.info("Json is not valid and cannot be deserialized. Returning null");
      return null;
    }
    if (reportRequest == null) {
      log.info("Returning null receipt from provider.");
    }
    return reportRequest;


  }

  @Provides
  @RequestScoped
  public Receipt getReceipt(Provider<JsonSerializer> jsonSerializer, Provider<HttpServletRequest> requestProvider, ReceiptContainer container) {
    HttpServletRequest request = requestProvider.get();

    String uri = request.getRequestURI();

    if ("/realPrintReceipt".equals(uri)) {

      String receiptKey = request.getParameter("receiptKey");
      log.info("RealPrintReceipt handles key: " + receiptKey);

      if (container.containsKey(receiptKey)) {
        log.info("Get receipt from container.");
        return container.getReceiptByKey(receiptKey);
      }
    }

    // get item from JSON
    Receipt receipt;
    try {

      log.info("Parsing receipt");
      byte[] stream = ByteStreams.toByteArray(request.getInputStream());

      String json = new String(stream, "UTF-8");

      System.out.println("JSON: " + json);

      Object deserialized = jsonSerializer.get().deserializeEntities(json, Receipt.class);
      receipt = (Receipt) deserialized;
    } catch (IOException e) {
      log.info("Json is not valid and cannot be deserialized. Returning null");
      return null;
    }
    if (receipt == null) {
      log.info("Returning null receipt from provider.");
    }
    return receipt;
  }

  @Provides
  @RequestScoped
  public ReceiptPrintService getReceiptPrintService(Provider<Receipt> receiptProvider, @OfficialReceipt ReceiptPrintService officialReceipt,
                                                    @FiscalReceipt ReceiptPrintService fiscalReceipt) {
    Receipt receipt = receiptProvider.get();
    ReceiptType type = ReceiptType.from(receipt.getReceiptType());
    if (type.equals(ReceiptType.BON)) {
      return officialReceipt;
    } else {
      return fiscalReceipt;
    }
  }

  @Provides
  @ServerPort
  public Integer getServerPort() {
    return 8080;
  }

}
