package com.clouway.servicebroker;

import com.clouway.common.DateUtil;
import com.clouway.servicebroker.print.Printer;
import com.evo.servicebroker.client.Receipt;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class QueuePrinterMonitorTest {
  private QueuePrinterMonitor monitor;
  private Receipt receipt;
  private Provider<Date> currentDate = Providers.of(DateUtil.newDateAndTime(2010, 6, 16, 9, 0, 0, 0));

  private Provider<Printer> printerProvider = Providers.of(Printer.with("printername"));

  private Provider<Receipt> receiptProvider = new Provider<Receipt>() {
    public Receipt get() {
      return receipt;
    }
  };

  @Before
  public void before() {
    monitor = new QueuePrinterMonitor(receiptProvider, printerProvider, currentDate);
  }

  @Test
  public void testAddReceiptWithSameKeyTwoTimes() {
    receipt = getReceipt();

    monitor.addReceipt();
    monitor.addReceipt();

    assertEquals(2, monitor.getQueueSize());
  }

  @Test
  public void testPrintReceiptWhoIsInQueueLessThenTwoHours() {
    receipt = getReceipt(DateUtil.newDateAndTime(2010, 6, 16, 7, 15, 0, 0));

    monitor.addReceipt();
    try {
      monitor.isMyTurn();
    } catch (ReceiptExpireException e) {
      fail();
    }

    assertEquals(1, monitor.getQueueSize());
  }

  @Test
  public void testThrowExceptionWhenReceiptIsMoreThanSixHoursInQueue() {
    receipt = getReceipt(DateUtil.newDateAndTime(2010, 6, 16, 2, 59, 0, 0));

    monitor.addReceipt();

    try {
      monitor.isMyTurn();
      fail();
    } catch (ReceiptExpireException e) {
      assertNotNull(e);
    }

    assertEquals(1, monitor.getQueueSize());
  }

  private Receipt getReceipt() {
    return getReceipt(DateUtil.newDateAndTime(2010, 6, 16, 9, 0, 0, 0));
  }

  private Receipt getReceipt(Date printingDate) {
    return Receipt.with("Key", "", null, "", "", printingDate, null, null, null, "");
  }
}
