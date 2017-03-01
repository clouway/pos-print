package com.clouway.pos.print;


import com.google.inject.Module;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class PosPrintApp {


  public static void main(String[] args) {
    PosPrintServer server = new PosPrintServer();
    server.startServer(4455,new Module[] { });
  }
}
