package com.clouway.servicebroker;

import com.evo.servicebroker.client.Receipt;
import com.evo.servicebroker.client.ReceiptItem;
import com.clouway.servicebroker.print.PrintService;
import com.google.inject.Provider;

import javax.inject.Inject;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class FiscalReceiptPrintService extends BaseReceiptPrintService {
  private Provider<PrintService> printService;

  @Inject
  public FiscalReceiptPrintService(Provider<PrintService> printService, PrintLineHelper lineHelper, Provider<Receipt> receiptProvider) {
    super(printService, lineHelper, receiptProvider);
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
  protected void printReceiptItem(ReceiptItem receiptItem, String department) {
    // service and amount
    printService.get().sellFree(receiptItem.getName(), receiptItem.getPrice().floatValue(), receiptItem.getQuantity().floatValue(), 0f ,department);
  }
}
