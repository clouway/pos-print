package com.clouway;

import com.clouway.pos.print.ReceiptPrintService;
import com.clouway.pos.print.client.Receipt;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertTrue;

/**
* @author Miroslav Genov (mgenov@gmail.com)
*/
class InMemoryReceiptPrintService implements ReceiptPrintService {

  private Set<String> receiptIdList = Sets.newHashSet();

  @Override
  public void printReceipt(Receipt receipt) {
    receiptIdList.add(receipt.getReceiptId());
  }

  public void assertHasReceivedReceiptForPrinting(String receiptId) {
    assertTrue("the provided receipt with id " + receiptId + " was not scheduled for printing",receiptIdList.contains(receiptId));
  }
}
