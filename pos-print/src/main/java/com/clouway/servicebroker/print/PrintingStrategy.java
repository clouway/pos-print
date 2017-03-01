package com.clouway.servicebroker.print;

/**
 * Strategies for printing messages using different communication links.
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public interface PrintingStrategy {

  PrintingModule getBindings();
  
}
