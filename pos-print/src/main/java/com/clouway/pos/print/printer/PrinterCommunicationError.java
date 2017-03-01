package com.clouway.pos.print.printer;

/**
 * PrinterCommunicationError is an error class that is thrown by the 
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class PrinterCommunicationError {

  private static final PrinterCommunicationError BROKEN_COMMUNICATION_ERROR = new PrinterCommunicationError("Връзката с принтера беше прекъсната.");
  private static final PrinterCommunicationError CLOSE_CONNECTION_ERROR = new PrinterCommunicationError("Връзката с принтера неможе да бъде затворена.");
  private static final PrinterCommunicationError OPEN_CONNECTION_ERROR = new PrinterCommunicationError("Връзката с принтера неможе да бъде установена.");


  public static PrinterCommunicationError brokenCommunication() {
    return BROKEN_COMMUNICATION_ERROR;
  }

  public static PrinterCommunicationError connectionCannotBeClosed() {
    return CLOSE_CONNECTION_ERROR;
  }

  public static PrinterCommunicationError connectionCannotBeOpened() {
    return OPEN_CONNECTION_ERROR;
  }
  
  private final String errorMessage;

  private PrinterCommunicationError(final String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PrinterCommunicationError that = (PrinterCommunicationError) o;
    
    return errorMessage.equals(that.errorMessage);
  }

  @Override
  public int hashCode() {
    return errorMessage.hashCode();
  }



}
