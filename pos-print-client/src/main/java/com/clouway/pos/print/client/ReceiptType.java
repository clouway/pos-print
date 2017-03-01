package com.clouway.pos.print.client;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public enum ReceiptType {
  BON(0, "Бон"), FISCAL_BON(1, "Фискален бон");

  private final Integer id;
  private final String name;

  ReceiptType(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

  public Integer value() {
    return id;
  }

  public static ReceiptType from(Integer id) {
    for (ReceiptType t : values()) {
      if (t.id.equals(id)) {
        return t;
      }
    }
    return null;
  }

  public static ReceiptType from(String id) {
    for (ReceiptType t : values()) {
      if (t.id.equals(Integer.parseInt(id))) {
        return t;
      }
    }
    return null;
  }

}
