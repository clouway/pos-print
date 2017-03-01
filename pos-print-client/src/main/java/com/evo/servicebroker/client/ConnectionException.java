package com.evo.servicebroker.client;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ConnectionException extends RuntimeException {
  private static final String INVALID_RESPONSE = "Възникна грешка при комуникацията със сървъра.";

  public static ConnectionException invalidResponse(){
    return new ConnectionException(INVALID_RESPONSE);
  }

  private String message;

  private ConnectionException() {
  }

  public ConnectionException(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
