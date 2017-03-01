package com.clouway.servicebroker.print;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public enum TextAlign {
  LEFT(0),RIGHT(1),CENTER(2);


  private final Integer value;

  TextAlign(Integer value) {
    this.value = value;
  }
  
  public Integer getValue() {
    return value;
  }
}
