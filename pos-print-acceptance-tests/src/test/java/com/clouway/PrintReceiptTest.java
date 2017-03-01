package com.clouway;

import com.clouway.pos.print.PosPrintServer;
import com.clouway.pos.print.ReceiptPrintService;
import com.clouway.pos.print.client.PosPrintClient;
import com.clouway.pos.print.client.PosPrintClientFactory;
import com.clouway.pos.print.client.Receipt;
import com.clouway.pos.print.client.ReceiptItem;
import com.clouway.pos.print.common.DateUtil;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.clouway.pos.print.client.Receipt.newReceipt;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class PrintReceiptTest {

  FakeTelcoNGServer telcoNG = new FakeTelcoNGServer(4444);
  PosPrintServer posPrintServer = new PosPrintServer();
  InMemoryReceiptPrintService receiptPrintService = new InMemoryReceiptPrintService();

  @Before
  public void initialize() {
    posPrintServer.startServer(5555, new Module[] { new AbstractModule() {
      @Override
      protected void configure() {
        bind(ReceiptPrintService.class).toInstance(receiptPrintService);
      }
    }});
    telcoNG.startServer();
  }

  @After
  public void cleanUp() {
    posPrintServer.stopServer();
    telcoNG.stopServer();
  }


  @Test
  public void printReceipt() {
    Receipt receipt = newReceipt().withReceiptId("12345").addItem(
            ReceiptItem.with(january(2012, 5), january(2012, 6), "test", 1d, 24.99d))
            .build();

    PosPrintClient client = PosPrintClientFactory.getServiceForServer("http://127.0.0.1:5555");
    client.scheduleReceiptForPrinting(
            receipt.getReceiptId(),telcoNG.getReceiptRetrieveCallbackUrl(receipt.getReceiptId())
    );

    telcoNG.pretendReceiptExists(receipt);

    receiptPrintService.assertHasReceivedReceiptForPrinting(receipt.getReceiptId());
  }

  private Date january(int year, int day) {
    return DateUtil.newDateAndTime(year,1,day,0,0,0,0);
  }
}
