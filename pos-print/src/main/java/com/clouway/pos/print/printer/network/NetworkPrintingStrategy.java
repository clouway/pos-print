package com.clouway.pos.print.printer.network;

import com.clouway.pos.print.printer.PrintingModule;
import com.clouway.pos.print.printer.PrintingStrategy;

/**
 * NetworkPrintingStrategy represents a printing strategy that is using a networking connection
 * as a communication layer. 
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class NetworkPrintingStrategy implements PrintingStrategy {

  /**
   * Gets all bindings for the current strategy.
   * @return networking printing module
   */
  public PrintingModule getBindings() {
    return new NetworkPrintingModule();
  }
}
