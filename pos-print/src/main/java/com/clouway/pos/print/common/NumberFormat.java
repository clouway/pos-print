package com.clouway.pos.print.common;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class NumberFormat {
  private static final String FORMAT = "0.00;-0.00";

  public static String formatDouble(Double value) {
    if(value == null){
      return "";
    }
    java.text.DecimalFormat df = new java.text.DecimalFormat(FORMAT);
    return df.format(value);
  }

}
