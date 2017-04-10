package com.clouway.pos.print.core;

/**
 * RegisterState is the requested state which need to be applied
 * to the register.
 * 
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public enum RegisterState {
  /* Keep the current state of the Register */
  KEEP,
  /* Clear the state of the Register */
  CLEAR
}
