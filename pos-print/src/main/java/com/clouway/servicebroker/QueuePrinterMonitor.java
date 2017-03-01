package com.clouway.servicebroker;

import com.clouway.common.CurrentDate;
import com.clouway.common.DateUtil;
import com.evo.servicebroker.client.Receipt;
import com.clouway.servicebroker.print.Printer;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * Monitor each printer and keep track on receipts that need to be printed on given printer.
 * Put all receipts for given printer in queue. Receipts in queue is not printed from here.
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
@Singleton
public class QueuePrinterMonitor implements PrinterMonitor {
  private final Logger log = Logger.getLogger(QueuePrinterMonitor.class.getName());

  private ConcurrentMap<String, Queue<String>> printers = new MapMaker()
          .concurrencyLevel(32)
          .makeMap();
  private Provider<Receipt> receiptProvider;
  private Provider<Printer> printerProvider;
  private Provider<Date> currentDateProvider;

  @Inject
  public QueuePrinterMonitor(Provider<Receipt> receiptProvider, Provider<Printer> printerProvider, @CurrentDate Provider<Date> currentDateProvider) {
    this.receiptProvider = receiptProvider;
    this.printerProvider = printerProvider;
    this.currentDateProvider = currentDateProvider;
  }

  /**
   * Add receipt in queue for printer.
   */
  public void addReceipt() {
    Printer printer = printerProvider.get();
    Receipt receipt = receiptProvider.get();

    // put printer in map if printer is not used until now
    printers.putIfAbsent(printer.getName(), new ConcurrentLinkedQueue<String>());
    Queue<String> queue = printers.get(printer.getName());

    log.info("Put in printer queue. Receipt key: " + receipt.generateUniqueId()
            + " Printed on: " + receipt.getPrintingDate()
            + " Size on queue for this printer is: " + getQueueSize());

    // put my receipt in printer queue
    queue.add(receipt.generateUniqueId());

  }

  /**
   * Check if receipt is next for printing or its not. Also check how much time receipt wait for printing. If receipt
   * wait too long for printing throws {@link ReceiptExpireException}. Receipt remains in queue
   * until someone call releasePrinter() method.
   *
   * @return true if receipt is next for printing and false otherwise.
   */
  public Boolean isMyTurn() {
    Printer printer = printerProvider.get();
    Receipt receipt = receiptProvider.get();

    if(isOldReceipt(receipt)){
      log.info("Receipt is expired. Receipt key: " + receipt.generateUniqueId() + " Printing date: " + receipt.getPrintingDate());
      throw new ReceiptExpireException();
    }

    Queue<String> queue = printers.get(printer.getName());

    log.info("Before peek, number in queue: " + queue.size());

    String receiptKey = queue.peek();

    log.info("Peek executed, number in queue: " + queue.size());
    if(receiptKey.equals(receipt.generateUniqueId())){
      return true;
    }

    return false;
  }

  /**
   * Remove first receipt for specified printer from queue.
   */
  public void releasePrinter() {
    Printer printer = printerProvider.get();
    String printerName = printer.getName();

    // get printing queue for current printer
    Queue<String> queue = printers.get(printerName);
    String receiptKey = queue.poll();
    
    if(receiptKey != null){
      log.info("Remove receipt from queue: Receipt key: " + receiptKey + " Size on queue for this printer is: " + getQueueSize());
    }
  }

  public int getQueueSize(){
    Printer printer = printerProvider.get();
    return printers.get(printer.getName()).size();
  }

  private boolean isOldReceipt(Receipt receipt){
    Date printDate = receipt.getPrintingDate();
    Date currentDate = currentDateProvider.get();

    int hours = DateUtil.hoursBetween(printDate, currentDate);

//    if(hours >= 6){
//      return true;
//    }
    return false;
  }
}
