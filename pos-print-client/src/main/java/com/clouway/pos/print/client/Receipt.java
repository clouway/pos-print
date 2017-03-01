package com.clouway.pos.print.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class Receipt {

  public static Builder newReceipt() {
    return new Builder();
  }

  public static class Builder {
    private String receiptId;
    private ReceiptDetails customerDetails;
    private String transactionNumber;
    private String cashierName;
    private Date printingDate;
    private Integer receiptType;
    private Double amount;
    private List<ReceiptItem> receiptItems = new ArrayList<ReceiptItem>();

    public Builder() {
    }

    public Builder withReceiptId(String receiptId) {
      this.receiptId = receiptId;
      return this;
    }

    public Builder withAmount(Double amount) {
      this.amount = amount;
      return this;
    }

    public Builder withTransactionNumber(String transactionNumber) {
      this.transactionNumber = transactionNumber;
      return this;
    }

    public Builder withCashierName(String cashierName) {
      this.cashierName = cashierName;
      return this;
    }

    public Builder withPrintingDate(Date printingDate) {
      this.printingDate = printingDate;
      return this;
    }

    public Builder addItem(ReceiptItem receiptItem) {
      this.receiptItems.add(receiptItem);
      return this;
    }

    public Builder addItems(ReceiptItem... receiptItem) {
      this.receiptItems.addAll(Arrays.asList(receiptItem));
      return this;
    }

    public Builder addItems(Collection<ReceiptItem> items) {
      this.receiptItems.addAll(items);
      return this;
    }

    public Receipt build() {
      Receipt receipt = new Receipt();
      receipt.receiptId = receiptId;
      receipt.amount = amount;
      receipt.receiptItems = receiptItems;
      return receipt;
    }
  }


  /**
   * Identifier for current receipt *
   */
  private String receiptId;

  /**
   * Ip address from where this receipt is printed *
   */
  private String printingIp;

  /**
   * Details about customer *
   */
  private ReceiptDetails customerDetails;

  /**
   * Number of transaction
   */
  private String transactionNumber;

  /**
   * Cashier real name
   */
  private String cashierName;

  /**
   * Date when this receipt is printed
   */
  private Date printingDate;

  /**
   * Type of the receipt bon or fiscal bon
   */
  private Integer receiptType;

  /**
   * Receipt items
   */
  private List<ReceiptItem> receiptItems;

  /**
   * Total amount for current receipt
   */
  private Double amount;

  /**
   * Printing state on current receipt 
   */
  private Integer printingState;

  private Receipt() {
  }

  public String getReceiptId() {
    return receiptId;
  }

  public String getPrintingIp() {
    return printingIp;
  }

  public ReceiptDetails getCustomerDetails() {
    return customerDetails;
  }

  public String getTransactionNumber() {
    return transactionNumber;
  }

  public String getCashierName() {
    return cashierName;
  }

  public Date getPrintingDate() {
    return printingDate;
  }

  public Integer getReceiptType() {
    return receiptType;
  }

  public List<ReceiptItem> getReceiptItems() {
    return new ArrayList<ReceiptItem>(receiptItems);
  }

  public Double getAmount() {
    return amount;
  }

  public Integer getPrintingState() {
    return printingState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Receipt receipt = (Receipt) o;

    if (amount != null ? !amount.equals(receipt.amount) : receipt.amount != null) return false;
    if (cashierName != null ? !cashierName.equals(receipt.cashierName) : receipt.cashierName != null) return false;
    if (customerDetails != null ? !customerDetails.equals(receipt.customerDetails) : receipt.customerDetails != null)
      return false;
    if (printingDate != null ? !printingDate.equals(receipt.printingDate) : receipt.printingDate != null) return false;
    if (printingIp != null ? !printingIp.equals(receipt.printingIp) : receipt.printingIp != null) return false;
    if (printingState != null ? !printingState.equals(receipt.printingState) : receipt.printingState != null)
      return false;
    if (receiptItems != null ? !receiptItems.equals(receipt.receiptItems) : receipt.receiptItems != null) return false;
    if (receiptType != null ? !receiptType.equals(receipt.receiptType) : receipt.receiptType != null) return false;
    if (transactionNumber != null ? !transactionNumber.equals(receipt.transactionNumber) : receipt.transactionNumber != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = printingIp != null ? printingIp.hashCode() : 0;
    result = 31 * result + (customerDetails != null ? customerDetails.hashCode() : 0);
    result = 31 * result + (transactionNumber != null ? transactionNumber.hashCode() : 0);
    result = 31 * result + (cashierName != null ? cashierName.hashCode() : 0);
    result = 31 * result + (printingDate != null ? printingDate.hashCode() : 0);
    result = 31 * result + (receiptType != null ? receiptType.hashCode() : 0);
    result = 31 * result + (receiptItems != null ? receiptItems.hashCode() : 0);
    result = 31 * result + (amount != null ? amount.hashCode() : 0);
    result = 31 * result + (printingState != null ? printingState.hashCode() : 0);
    return result;
  }
}
