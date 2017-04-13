package com.clouway.pos.print.core;

/**
 * An exceptional class used to indicate errors which are occurring due reaching
 * the timeout of the operation.
 * 
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RequestTimeoutException extends RuntimeException {
  
  public RequestTimeoutException(String message) {
    super(message);
  }
}
