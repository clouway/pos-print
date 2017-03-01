package com.clouway.servicebroker.print;

/**
 * 
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class CommunicationErrorException extends RuntimeException{
  private final PrinterCommunicationError error;

  public CommunicationErrorException(PrinterCommunicationError error) {
    this.error = error;
  }

  public PrinterCommunicationError getCommunicationError() {
    return error;
  }
  
}
