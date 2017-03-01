package com.clouway.pos.print.printer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * PrinterConnection represents the connection between the printer and the application. This connection would be used
 * by the printing logic to send different kind of commands to the printer and also the read the response from there execution.
 * 
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public final class PrinterConnection {
  private final InputStream in;
  private final OutputStream out;

  public static PrinterConnection newConnection(InputStream in, OutputStream out){
    return new PrinterConnection(in, out);
  }


  private PrinterConnection(InputStream in, OutputStream out) {
    this.in = in;
    this.out = out;
  }

  public InputStream getInputStream() {
    return in;
  }

  public OutputStream getOutputStream() {
    return out;
  }


}
