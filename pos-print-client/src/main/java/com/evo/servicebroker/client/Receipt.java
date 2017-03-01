package com.evo.servicebroker.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class Receipt {

  public static class Builder {
    private String id;
    private String printingIp;
    private ReceiptDetails customerDetails;
    private String transactionNumber;
    private String cashierName;
    private Date printingDate;
    private Integer receiptType;
    private Double amount;
    private Integer printingState;
    private String department;
    private List<ReceiptItem> receiptItems = new ArrayList<ReceiptItem>();

    public Builder(String id,
                   String printingIp,
                   ReceiptDetails customerDetails,
                   String transactionNumber,
                   String cashierName,
                   Date printingDate,
                   Double amount, ReceiptType receiptType) {
      this.id = id;
      this.printingIp = printingIp;
      this.customerDetails = customerDetails;
      this.transactionNumber = transactionNumber;
      this.cashierName = cashierName;
      this.printingDate = printingDate;
      this.amount = amount;
      this.receiptType = receiptType.value();
      this.printingState = PrintingState.QUEUE.value();
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

    public Builder setPrintingState(PrintingState printingState){
      this.printingState = printingState.value();
      return this;
    }

    public Builder setPrintingState(String department){
      this.department = department;
      return this;
    }

    public Receipt build() {
      Receipt receipt = Receipt.with(id, printingIp, customerDetails, transactionNumber, cashierName, printingDate, receiptType, receiptItems, amount, department);
      receipt.printingState = printingState;
      return receipt;
    }
  }


  public static Receipt with(String key, String printingIp,
                             ReceiptDetails customerDetails,
                             String transactionNumber, String cashierName,
                             Date printingDate, Integer receiptType,
                             List<ReceiptItem> receiptItems,
                             Double amount,
                             String department) {
    Receipt receipt = new Receipt();
    receipt.key = key;
    receipt.printingIp = printingIp;
    receipt.customerDetails = customerDetails;
    receipt.transactionNumber = transactionNumber;
    receipt.cashierName = cashierName;
    receipt.printingDate = printingDate;
    receipt.receiptType = receiptType;
    receipt.receiptItems = receiptItems;
    receipt.amount = amount;
    receipt.department = department;
    return receipt;
  }


  public static Receipt withIsPrintedState(Receipt existedReceipt, PrintingState printingState){
    Receipt receipt = from(existedReceipt);
    receipt.printingState = printingState.value();
    return receipt;
  }

  public static Receipt from(Receipt existedReceipt){
    Receipt receipt = new Receipt();
    receipt.key = existedReceipt.key;
    receipt.printingIp = existedReceipt.printingIp;
    receipt.customerDetails = existedReceipt.customerDetails;
    receipt.transactionNumber = existedReceipt.transactionNumber;
    receipt.cashierName = existedReceipt.cashierName;
    receipt.printingDate = existedReceipt.printingDate;
    receipt.receiptType = existedReceipt.receiptType;
    receipt.receiptItems = existedReceipt.receiptItems;
    receipt.amount = existedReceipt.amount;
    receipt.printingState = existedReceipt.printingState;
    receipt.department = existedReceipt.department;
    return receipt;
  }

  /**
   * Identifier for current receipt *
   */
  private String key;

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

  /**
   * the printing department in the fiscal printer
   */
  private String department;

  private Receipt() {
  }

  public String getKey() {
    return key;
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

  public String getDepartment() {
    return department;
  }

  public String generateUniqueId(){
    return key + receiptType;
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
    if (department!= null ? !department.equals(receipt.department) : receipt.department != null)

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
    result = 31 * result + (department != null ? department.hashCode() : 0);
    return result;
  }
}
