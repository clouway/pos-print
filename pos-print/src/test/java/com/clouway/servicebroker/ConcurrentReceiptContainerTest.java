package com.clouway.servicebroker;

import com.evo.servicebroker.client.Receipt;
import com.evo.servicebroker.client.ReceiptType;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ConcurrentReceiptContainerTest {

  private ConcurrentReceiptContainer container;

  @Before
  public void before(){
    container = new ConcurrentReceiptContainer();
  }

  @Test
  public void testAddReceiptInContainer(){
    Receipt receipt = getReceipt("keyone", ReceiptType.BON);
    container.addReceipt(receipt);

    assertTrue(container.containsReceipt(receipt));

    assertEquals(receipt, container.getReceipt(receipt));
  }

  @Test
  public void testRemoveReceiptFromContainer(){
    Receipt receipt = getReceipt("keyone", ReceiptType.BON);
    container.addReceipt(receipt);

    assertTrue(container.containsReceipt(receipt));

    Receipt containedReceipt = container.removeReceipt(receipt);

    assertFalse(container.containsReceipt(receipt));

    assertEquals(receipt, containedReceipt);
  }

  @Test
  public void testAddTwoReceiptsInContainer(){
    Receipt receiptOne = getReceipt("keyone", ReceiptType.BON);
    Receipt receiptTwo = getReceipt("keyTwo", ReceiptType.FISCAL_BON);

    container.addReceipt(receiptOne);
    container.addReceipt(receiptTwo);

    assertEquals(2, container.getSize());
  }

  @Test
  public void testAddReceiptInContainerTwoTimesOnceLikeBonAndAgainLikeFiscalBon(){
    Receipt bon = getReceipt("keyone", ReceiptType.BON);
    Receipt fiscalBon = getReceipt("keyone", ReceiptType.FISCAL_BON);

    container.addReceipt(bon);
    container.addReceipt(fiscalBon);

    assertEquals(2, container.getSize());
  }

  private Receipt getReceipt(String receiptKey, ReceiptType type){
    return Receipt.with(receiptKey, "", null, "", "", null, type.value(), null, null,"");
  }
}
