package com.clouway.pos.print.core;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class Receipt {

  public static Builder newReceipt() {
    return new Builder();
  }

  public static class Builder {
    private String receiptId = "";
    private Double amount = 0.0d;
    private String currency = "USD";

    private List<String> prefixLines = new ArrayList<>();
    private List<String> suffixLines = new ArrayList<>();

    private List<ReceiptItem> receiptItems = new ArrayList<>();


    public Builder() {

    }

    public Builder currency(String currency) {
      this.currency = currency;
      return this;
    }

    public Builder withReceiptId(String receiptId) {
      this.receiptId = receiptId;
      return this;
    }

    public Builder prefixLines(List<String> details) {
      this.prefixLines = details;
      return this;
    }

    public Builder suffixLines(List<String> lines) {
      this.suffixLines = lines;
      return this;
    }

    public Builder withAmount(Double amount) {
      this.amount = amount;
      return this;
    }

    public Builder addItem(ReceiptItem receiptItem) {
      this.receiptItems.add(receiptItem);
      return this;
    }

    public Builder addItems(ReceiptItem... receiptItems) {
      this.receiptItems.addAll(Arrays.asList(receiptItems));
      return this;
    }

    public Builder addItems(List<ReceiptItem> items) {
      this.receiptItems.addAll(items);
      return this;
    }

    public Receipt build() {
      Receipt receipt = new Receipt();
      receipt.receiptId = receiptId;
      receipt.amount = amount;
      receipt.currency = currency;
      receipt.prefixLines = prefixLines;
      receipt.receiptItems = receiptItems;
      receipt.suffixLines = suffixLines;

      return receipt;
    }
  }


  /**
   * Identifier for current receipt *
   */
  private String receiptId;

  /**
   * A list of lines that will be printed before receipt items.
   */
  private List<String> prefixLines;

  /**
   * Receipt items
   */
  private List<ReceiptItem> receiptItems;

  /**
   * A list of lines that will be printed after receipt items.
   */
  private List<String> suffixLines;

  /**
   * Currency of the amount
   */
  private String currency;

  /**
   * Total amount of the receipt
   */
  private Double amount;

  private Receipt() {
  }

  public List<String> prefixLines() {
    return prefixLines;
  }

  public String getReceiptId() {
    return receiptId;
  }

  public List<ReceiptItem> getReceiptItems() {
    return receiptItems;
  }

  public List<String> suffixLines() {
    return suffixLines;
  }

  public Double getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Receipt receipt = (Receipt) o;
    return Objects.equals(receiptId, receipt.receiptId) &&
            Objects.equals(receiptItems, receipt.receiptItems) &&
            Objects.equals(amount, receipt.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(receiptId, receiptItems, amount);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("receiptId", receiptId)
      .add("prefixLines", prefixLines)
      .add("receiptItems", receiptItems)
      .add("suffixLines", suffixLines)
      .add("currency", currency)
      .add("amount", amount)
      .toString();
  }
}