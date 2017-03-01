package com.clouway.pos.print;

import com.clouway.pos.print.client.Receipt;
import com.clouway.pos.print.client.ReceiptItem;
import com.clouway.pos.print.printer.PrintService;
import com.google.inject.Provider;

import javax.inject.Inject;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class FiscalReceiptPrintService extends BaseReceiptPrintService {
  private Provider<PrintService> printService;

  @Inject
  public FiscalReceiptPrintService(Provider<PrintService> printService, PrintLineHelper lineHelper) {
    super(printService, lineHelper);
    this.printService = printService;
  }

  @Override
  protected void startPrinting() {
    printService.get().openFiscalBon();
  }

  @Override
  protected void printAtEnd(Receipt receipt, Double itemsSum) {
  }

  @Override
  protected void endPrinting() {
    printService.get().closeFiscalBon();
    printLineFeed();
    printLineFeed();
    printLineFeed();
    printLineFeed();
    printLineFeed();
  }

  @Override
  protected void printReceiptItem(ReceiptItem receiptItem) {
    // service and amount
    printService.get().sellFree(receiptItem.getName(), receiptItem.getPrice().floatValue(), receiptItem.getQuantity().floatValue(), 0f);
  }
}
