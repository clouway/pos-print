package com.evo.servicebroker.client;

import java.util.Set;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ErrorResponseException extends RuntimeException {
  private Set<String> errors;

  public ErrorResponseException(Set<String> errors) {
    this.errors = errors;
  }

  public boolean hasErrors() {
    if(errors == null || errors.size() == 0){
      return false;
    }
    return true;
  }

  public Set<String> getErrors() {
    return errors;
  }
}
