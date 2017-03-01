package com.clouway.servicebroker;

import com.clouway.common.DateConverter;
import com.clouway.servicebroker.print.PrintService;
import com.clouway.servicebroker.print.TextAlign;
import com.evo.servicebroker.client.Receipt;
import com.evo.servicebroker.client.ReceiptDetails;
import com.evo.servicebroker.client.ReceiptItem;
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
  private static  final String DEFAULT_DEPARTMENT = "02";
  private Provider<PrintService> printService;
  private PrintLineHelper lineHelper;
  private Provider<Receipt> receiptProvider;
  private Double itemsSum = 0d;

  protected BaseReceiptPrintService(Provider<PrintService> printService,
                                    PrintLineHelper lineHelper,
                                    Provider<Receipt> receiptProvider) {
    this.printService = printService;
    this.lineHelper = lineHelper;
    this.receiptProvider = receiptProvider;
  }

  /**
   * Printing given receipt.
   */
  public void printReceipt() {
    itemsSum = 0d;

    printService.get().connect();
    log.info("Print service connect OK!");

    Receipt receipt = receiptProvider.get();

    try {
//      printHeader(receipt);

      startPrinting();

      if(receipt.getDepartment()!=null && receipt.getDepartment().equals("01")){

        printMtelReceipt(receipt);

      }else {

        printNormalBillReceipt(receipt);

      }
//      for (ReceiptItem item : receipt.getReceiptItems()) {
//        printReceiptItem(item);
//      }

//      printPeriods(receipt);
//      printAtEnd(receipt, itemsSum);

      endPrinting();
    } catch (RuntimeException e) {
      log.log(Level.SEVERE, "Printing error!", e);
      throw e;
    }finally {
      log.info("Print service disconnect!");
      printService.get().disconnect();
    }
  }

  private void printMtelReceipt(Receipt receipt) {
    printFiscalText("Доверител:");
    printFiscalText("Мобилтел ЕАД \"Б\"");

    ReceiptItem item = receipt.getReceiptItems().get(0);
    String paymentBase = item.getName();
    Double amount = Double.valueOf(String.format("%.2f", item.getPrice()));

    log.info("Printing receipt item with amount of: " + amount);

    printReceiptItem(ReceiptItem.with(new Date(), new Date(), paymentBase, 1d, amount), receipt.getDepartment());

  }

  private void printNormalBillReceipt(Receipt receipt) {
      ReceiptDetails details = receipt.getCustomerDetails();
      // customer
      printFiscalText(lineHelper.customer(details.getCustomerName()));
      // address
      printFiscalText(lineHelper.address(details.getAddress()));
      // contract number and date
      printFiscalText((lineHelper.contractNoAndDate(details.getContractNumber(), details.getContractDate())));
      // cashier
      printFiscalText(lineHelper.cashier(receipt.getCashierName()));

      Double amount = 0d;
      for (ReceiptItem item : receipt.getReceiptItems()) {
        amount += item.getQuantity() * item.getPrice();
      }

    log.info("Printing receipt item with amount of: " + amount);

    String department = fetchDepartment(receipt);

    printReceiptItem(ReceiptItem.with(new Date(), new Date(), "услуга",1d,Double.valueOf(String.format("%.2f",amount))),department);
  }

  private String fetchDepartment(Receipt receipt) {
    if (receipt.getDepartment() != null) {
      return receipt.getDepartment();
    }
    return DEFAULT_DEPARTMENT;
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
        printReceiptItem(item, DEFAULT_DEPARTMENT);
      }
    }
    // line
    printTextLeft(lineHelper.line());

  }

  private Map<String, List<ReceiptItem>> sortItems(Receipt receipt){
    List<ReceiptItem> items = receipt.getReceiptItems();

    Map<String, List<ReceiptItem>> sortedItems = new TreeMap<String, List<ReceiptItem>>();

    for(ReceiptItem item : items){
      String from = DateConverter.convertToYearMonthDay(item.getFrom());
      String to = DateConverter.convertToYearMonthDay(item.getTo());

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

  public void printFiscalText(String text) {
    printService.get().printFiscalText(text);
  }

  public void printLineFeed(){
    printService.get().lineFeed();
  }

  protected abstract void startPrinting();

  protected abstract void printAtEnd(Receipt receipt, Double itemsSum);

  protected abstract void endPrinting();

  protected abstract void printReceiptItem(ReceiptItem receiptItem, String department);
}
