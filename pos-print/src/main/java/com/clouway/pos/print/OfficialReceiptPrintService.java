package com.clouway.pos.print;

import com.clouway.pos.print.client.Receipt;
import com.clouway.pos.print.client.ReceiptItem;
import com.clouway.pos.print.common.FormatsDates;
import com.clouway.pos.print.printer.PrintService;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Date;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class OfficialReceiptPrintService extends BaseReceiptPrintService{
  private PrintLineHelper lineHelper;

  @Inject
  public OfficialReceiptPrintService(Provider<PrintService> printService, PrintLineHelper lineHelper) {
    super(printService, lineHelper);
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
    String receiptDate = FormatsDates.toYearMonthDayHourSecond(date);

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
  protected void printReceiptItem(ReceiptItem receiptItem) {
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
