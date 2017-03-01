package com.clouway.servicebroker;

import com.evo.servicebroker.client.Receipt;

/**
 * Contains receipt that is received for printing.
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface ReceiptContainer {
  /**
   * Adds receipt to container.
   *
   * @param receipt to be added.
   */
  void addReceipt(Receipt receipt);

  /**
   * Remove receipt from container.
   *
   * @param receipt to be removed.
   * @return removed receipt.
   */
  Receipt removeReceipt(Receipt receipt);

  /**
   * Get receipt by given receipt key.
   *
   * @param receiptKey key of the receipt.
   * @return receipt.
   */
  Receipt getReceiptByKey(String receiptKey);

  /**
   * Check if receipt with key is found in container.
   *
   * @param receiptKey key.
   * @return true is found or false otherwise.
   */
  boolean containsKey(String receiptKey);
  
  /**
   * Return true if receipt is in container and false otherwise.
   *
   * @param receipt
   * @return true or false.
   */
  boolean containsReceipt(Receipt receipt);

  /**
   * Return receipt with same key from container.
   *
   * @param receipt from who key to be formed.
   * @return receipt from container.
   */
  Receipt getReceipt(Receipt receipt);

  /**
   * Return number of receipts in the container.
   *
   * @return size of the container.
   */
  int getSize();
}
