package com.clouway.servicebroker.print.internal;

/**
 *  PrinterConnector is a printer connector class that is responsible for the establishing a connection to a local or remote printer. 
 * 
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface PrinterConnector {
  /**
   * Opens connection to the printer
   *
   * @return connection with printer.
   */
  void openConnection();

  /**
   * Closes connection to the printer
   */
  void closeConnection();

   /**
   * Gets the current printer connection.
   * @return the current printer connection
   */
  PrinterConnection getCurrentConnection();
}
