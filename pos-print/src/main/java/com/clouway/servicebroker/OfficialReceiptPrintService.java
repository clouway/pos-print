package com.clouway.servicebroker;

import com.clouway.common.DateConverter;
import com.evo.servicebroker.client.Receipt;
import com.evo.servicebroker.client.ReceiptItem;
import com.clouway.servicebroker.print.PrintService;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Date;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class OfficialReceiptPrintService extends BaseReceiptPrintService{
  private PrintLineHelper lineHelper;

  @Inject
  public OfficialReceiptPrintService(Provider<PrintService> printService, PrintLineHelper lineHelper, Provider<Receipt> receiptProvider) {
    super(printService, lineHelper, receiptProvider);
    this.lineHelper = lineHelper;
  }

  @Override
  protected void startPrinting() {
    printCenter("С М Е Т К А");
  }

  @Override
  protected void printAtEnd(Receipt receipt, Double itemsSum) {
    // can be performed check between sum in the receipt and itemsSum
    printTextRight(lineHelper.total(itemsSum));
    printTextLeft(lineHelper.inCash(itemsSum));

    Date date = receipt.getPrintingDate();
    String receiptDate = DateConverter.convertToDayMonthYear(date);

    printTextLeft(receiptDate);
    printLineFeed();
    printLineFeed();
    printLineFeed();
    printLineFeed();
    printLineFeed();
  }

  @Override
  protected void endPrinting() {
    
  }

  @Override
  protected void printReceiptItem(ReceiptItem receiptItem, String department) {
    printTextLeft(receiptItem.getName());
    Double quantity = receiptItem.getQuantity();
    Double price = receiptItem.getPrice();

    if(!quantity.equals(1d)){
      printTextRight(lineHelper.charge(price, quantity));
    }
    Double total = price * quantity;
    printTextLeft(lineHelper.itemSum(total));
  }
}
