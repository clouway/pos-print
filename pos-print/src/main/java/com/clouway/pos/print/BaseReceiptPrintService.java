package com.clouway.pos.print;

import com.clouway.pos.print.client.Receipt;
import com.clouway.pos.print.client.ReceiptDetails;
import com.clouway.pos.print.client.ReceiptItem;
import com.clouway.pos.print.common.FormatsDates;
import com.clouway.pos.print.printer.PrintService;
import com.clouway.pos.print.printer.TextAlign;

import com.google.inject.Provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
abstract class BaseReceiptPrintService implements ReceiptPrintService {
  private final Logger log = Logger.getLogger(BaseReceiptPrintService.class.getSimpleName());
  private Provider<PrintService> printService;
  private PrintLineHelper lineHelper;
  private Double itemsSum = 0d;

  protected BaseReceiptPrintService(Provider<PrintService> printService,
                                    PrintLineHelper lineHelper) {
    this.printService = printService;
    this.lineHelper = lineHelper;
  }

  /**
   * Printing given receipt.
   */
  public void printReceipt(Receipt receipt) {
    itemsSum = 0d;

    printService.get().connect();
    log.info("Print service connect OK!");

    try {
      printHeader(receipt);

      startPrinting();

      printPeriods(receipt);
      printAtEnd(receipt, itemsSum);

      endPrinting();
    } catch (RuntimeException e) {
      log.log(Level.SEVERE, "Printing error!", e);
      throw e;
    }finally {
      log.info("Print service disconnect!");
      printService.get().disconnect();
    }
  }

  private void printHeader(Receipt receipt) {
    ReceiptDetails details = receipt.getCustomerDetails();
    // customer
    printTextLeft(lineHelper.customer(details.getCustomerName()));

    // address
    printTextLeft(lineHelper.address(details.getAddress()));

    // contract number and date
    printTextLeft((lineHelper.contractNoAndDate(details.getContractNumber(), details.getContractDate())));

    // line
    printTextLeft(lineHelper.line());

    // transaction number
    printTextLeft(lineHelper.transactionNo());

    printTextLeft(receipt.getTransactionNumber());

    // cashier
    printTextLeft(lineHelper.cashier(receipt.getCashierName()));

    // line
//    printTextLeft(lineHelper.line());
  }

  private void printPeriods(Receipt receipt) {
    // line
    printTextLeft(lineHelper.line());
    Map<String, List<ReceiptItem>> sortedItems = sortItems(receipt);

    for(String key : sortedItems.keySet()){
      List<ReceiptItem> items = sortedItems.get(key);

      if(items.size() > 0){ // probably not needed check.
        Date from = items.get(0).getFrom();
        Date to = items.get(0).getTo();
        if(from != null && to != null){
          // period from - to
          String period = lineHelper.period(from, to);
          printTextLeft(period);
          log.info("Date from: " + from.toString() + " to: " + to.toString());
          log.info("Period: " + period);
        }
      }

      for(ReceiptItem item : items){

        if(item.getPrice() == null){
          log.info("Receipt item price is null!");
        }

        if(item.getQuantity() == null){
          log.info("Receipt item quantity is null!");
        }

        itemsSum = itemsSum + item.getPrice() * item.getQuantity();
        printReceiptItem(item);
      }
    }
    // line
    printTextLeft(lineHelper.line());

  }

  private Map<String, List<ReceiptItem>> sortItems(Receipt receipt){
    List<ReceiptItem> items = receipt.getReceiptItems();

    Map<String, List<ReceiptItem>> sortedItems = new TreeMap<String, List<ReceiptItem>>();

    for(ReceiptItem item : items){
      String from = FormatsDates.toYearMonthDayHourSecond(item.getFrom());
      String to = FormatsDates.toYearMonthDayHourSecond(item.getTo());

      String key = from + " " + to;

      if(!sortedItems.containsKey(key)){
        List<ReceiptItem> list = new ArrayList<ReceiptItem>();
        sortedItems.put(key, list);
      }

      List<ReceiptItem> mapValue = sortedItems.get(key);
      mapValue.add(item);
    }
    

    return sortedItems;
  }

  public void printTextLeft(String line) {
    printService.get().printText(line, TextAlign.LEFT);
  }

  public void printTextRight(String line) {
    printService.get().printText(line, TextAlign.RIGHT);
  }

  public void printCenter(String line){
    printService.get().printText(line, TextAlign.CENTER);
  }

  public void printLineFeed(){
    printService.get().lineFeed();
  }

  protected abstract void startPrinting();

  protected abstract void printAtEnd(Receipt receipt, Double itemsSum);

  protected abstract void endPrinting();

  protected abstract void printReceiptItem(ReceiptItem receiptItem);
}
