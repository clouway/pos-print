package com.clouway.pos.print.printer;

/**
 * Strategies for printing messages using different communication links.
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public interface PrintingStrategy {

  PrintingModule getBindings();
  
}
