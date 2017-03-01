package com.evo.servicebroker.client;

import java.util.Date;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ReceiptItem {

  public static ReceiptItem with(Date from, Date to, String name, Double quantity, Double price){
    ReceiptItem receiptItem = new ReceiptItem();
    receiptItem.from = from;
    receiptItem.to = to;
    receiptItem.name = name;
    receiptItem.quantity = quantity;
    receiptItem.price = price;
    return receiptItem;
  }

  /** If this item is for period this tells when period start **/
  private Date from;

  /** If this item is for period this tells when period end **/
  private Date to;

  /** Name of the item **/
  private String name;

  /** Item quantity **/
  private Double quantity;

  /** Item price **/
  private Double price;

  private ReceiptItem() {
  }

  public Date getFrom() {
    return from;
  }

  public Date getTo() {
    return to;
  }

  public String getName() {
    return name;
  }

  public Double getQuantity() {
    return quantity;
  }

  public Double getPrice() {
    return price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ReceiptItem that = (ReceiptItem) o;

    if (from != null ? !from.equals(that.from) : that.from != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (price != null ? !price.equals(that.price) : that.price != null) return false;
    if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;
    if (to != null ? !to.equals(that.to) : that.to != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = from != null ? from.hashCode() : 0;
    result = 31 * result + (to != null ? to.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
    result = 31 * result + (price != null ? price.hashCode() : 0);
    return result;
  }
}
