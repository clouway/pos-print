package com.clouway.servicebroker.print;

/**
 * A sample printer class that is representing a sample printer.
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class Printer {
  
  public static Printer with(String name) {
    Printer printer = new Printer(name);
    return printer;
  }

  private final String name;


  private Printer(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
