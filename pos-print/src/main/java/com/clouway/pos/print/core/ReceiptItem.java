package com.clouway.pos.print.core;

import com.google.common.base.Objects;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@clouway.com)
 */
public class ReceiptItem {

  public static Builder newItem() {
    return new Builder();
  }

  public static class Builder {
    private String name = "";
    private Double quantity = 1d;
    private Double price = 1d;
    private Double vat = 20d;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder quantity(Double quantity) {
      this.quantity = quantity;
      return this;
    }

    public Builder price(Double price) {
      this.price = price;
      return this;
    }

    public Builder vat(Double vat) {
      this.vat = vat;
      return this;
    }

    public ReceiptItem build() {
      return new ReceiptItem(this);
    }

  }

  private ReceiptItem(Builder builder) {
    this.name = builder.name;
    this.price = builder.price;
    this.quantity = builder.quantity;
  }

  /**
   * Name of the item
   **/
  private String name;

  /**
   * Item quantity
   **/
  private Double quantity;

  /**
   * Item price
   **/
  private Double price;

  /**
   *  Value added tax
   */
  private Double vat;

  public String getName() {
    return name;
  }

  public Double getQuantity() {
    return quantity;
  }

  public Double getPrice() {
    return price;
  }

  public Double getVat() {
    return vat;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReceiptItem that = (ReceiptItem) o;
    return Objects.equal(name, that.name) &&
      Objects.equal(quantity, that.quantity) &&
      Objects.equal(price, that.price) &&
      Objects.equal(vat, that.vat);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, quantity, price, vat);
  }
}
