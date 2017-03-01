package com.clouway.servicebroker;

import com.evo.servicebroker.client.Receipt;
import com.google.common.collect.MapMaker;
import com.google.inject.Singleton;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Contains receipt that is received for printing.
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
@Singleton
public class ConcurrentReceiptContainer implements ReceiptContainer {

  private ConcurrentMap<String, Receipt> receipts = new MapMaker()
          .concurrencyLevel(32)
          .expiration(2, TimeUnit.HOURS)
          .makeMap();

  /**
   * Adds receipt to container.
   *
   * @param receipt to be added.
   */
  public void addReceipt(Receipt receipt) {
    receipts.put(receipt.generateUniqueId(), receipt);
  }

  /**
   * Remove receipt from container.
   *
   * @param receipt to be removed.
   * @return removed receipt.
   */
  public Receipt removeReceipt(Receipt receipt) {
    return receipts.remove(receipt.generateUniqueId());
  }

  /**
   * Get receipt by given receipt key.
   *
   * @param receiptKey key of the receipt.
   * @return receipt.
   */
  public Receipt getReceiptByKey(String receiptKey) {
    return receipts.get(receiptKey);
  }

  /**
   * Check if receipt with key is found in container.
   *
   * @param receiptKey key.
   * @return true is found or false otherwise.
   */
  public boolean containsKey(String receiptKey){
    return receipts.containsKey(receiptKey);
  }

  /**
   * Return true if receipt is in container and false otherwise.
   *
   * @param receipt
   * @return true or false.
   */
  public boolean containsReceipt(Receipt receipt) {
    return receipts.containsKey(receipt.generateUniqueId());
  }

  /**
   * Return receipt with same key from container.
   *
   * @param receipt from who key to be formed.
   * @return receipt from container.
   */
  public Receipt getReceipt(Receipt receipt) {
    return receipts.get(receipt.generateUniqueId());
  }

  /**
   * Return number of receipts in the container.
   *
   * @return size of the container.
   */
  public int getSize() {
    return receipts.size();
  }
}
