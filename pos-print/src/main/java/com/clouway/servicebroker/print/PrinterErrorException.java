package com.clouway.servicebroker.print;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class PrinterErrorException extends RuntimeException{
  private final Set<PrinterError> errors = new HashSet<PrinterError>();

  public PrinterErrorException(PrinterError error) {
    errors.add(error);
  }

  public void addError(PrinterError error) {
    errors.add(error);
  }

  public Set<PrinterError> getErrors() {
    return new HashSet<PrinterError>(errors);
  }

  public boolean containsError(PrinterError error) {
    return errors.contains(error);
  }
}
