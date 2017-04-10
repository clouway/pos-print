package com.clouway.pos.print.client;

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


    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (price != null ? !price.equals(that.price) : that.price != null) return false;
    if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
    result = 31 * result + (price != null ? price.hashCode() : 0);
    return result;
  }
}
