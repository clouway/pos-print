package com.clouway.servicebroker;

import com.evo.servicebroker.client.PrintResponse;
import com.evo.servicebroker.client.Receipt;
import com.evo.servicebroker.client.ReceiptDetails;
import com.evo.servicebroker.client.ReceiptItem;
import com.evo.servicebroker.client.ReceiptType;
import com.evo.servicebroker.client.ServiceBrokerClient;
import com.evo.servicebroker.client.ServiceBrokerClientFactory;
import com.clouway.servicebroker.print.PrintService;
import com.clouway.servicebroker.print.PrinterError;
import com.clouway.servicebroker.print.PrinterErrorException;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.util.Modules;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.*;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
@RunWith(JMock.class)
public class PrintingContentExchangeTest {
  private Mockery context = new JUnit4Mockery();

  @Inject
  private ServiceBroker broker;

  private String address = "http://localhost:4040";
  private Integer port = 4040;
  private ServiceBrokerClient client = ServiceBrokerClientFactory.getServiceForServer(address);


  private PrintService printService;

  @Before
  public void before() {
    printService = context.mock(PrintService.class);

    Module module = Modules.override(new ServiceBrokerModule()).with(
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(PrintService.class).toInstance(printService);
              }

              @Provides
              @ServerPort
              public Integer getServerPort() {
                return port;
              }

            });


    Guice.createInjector(module).injectMembers(this);
    broker.startServer();
  }

  @After
  public void after() {
    broker.stopServer();
  }

  @Test
  public void testSendReceiptForPrinting() {
    Receipt receipt = getReceipt("receiptkey", ReceiptType.BON);

    PrintResponse response = client.print(receipt, Receipt.class);

    assertTrue(response.isSuccess());
    assertFalse(response.isErrorResponse());
    assertEquals(PrintResponse.success().getInfo().iterator().next().getInfoMessage(), response.getInfo().iterator().next().getInfoMessage());
  }

  @Test
  public void testPrintingReceiptErrorResponse() throws InterruptedException {
    Receipt receipt = getReceipt("receiptkey", ReceiptType.BON);

    context.checking(new Expectations() {{
      oneOf(printService).connect();
      will(throwException(new PrinterErrorException(PrinterError.deviceError())));
    }});

    printService.connect();

    PrintResponse response = client.print(receipt, Receipt.class);
    assertNotNull(response);

    assertTrue(response.isErrorResponse());
    assertFalse(response.isSuccess());
    assertEquals(PrinterError.deviceError().getErrorMessage(), response.getInfo().iterator().next().getInfoMessage());
  }

  private Receipt getReceipt(String receiptKey, ReceiptType type) {
    ReceiptDetails details = ReceiptDetails.with("Customer name", "Address", "1234567", new Date());
    return Receipt.with(receiptKey, "127.0.0.1", details, "123456789", "", new Date(), type.value(), new ArrayList<ReceiptItem>(), 0d,"");
  }
}
