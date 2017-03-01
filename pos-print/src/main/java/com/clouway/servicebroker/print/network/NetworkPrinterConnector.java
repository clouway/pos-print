package com.clouway.servicebroker.print.network;

import com.clouway.servicebroker.print.internal.PrinterConnection;
import com.clouway.servicebroker.print.internal.PrinterConnector;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * NetworkPrinterConnector is a printer connector class
 *  
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class NetworkPrinterConnector implements PrinterConnector {
  private final Logger log = Logger.getLogger(NetworkPrinterConnector.class.getName());
  private final PrinterSocket socket;
  private InputStream in;
  private OutputStream out;
  
  public NetworkPrinterConnector(PrinterSocket socket) {
    this.socket = socket;
  }

  public void openConnection() {
    log.info("Connect to socket connection!");
    socket.connect();
  }

  public void closeConnection() {
    log.info("Close socket connection!");
    socket.close();
  }
 
  public PrinterConnection getCurrentConnection() {
    in = socket.getInputStream();
    out = socket.getOutputStream();
    return PrinterConnection.newConnection(in, out);
  }
  
}
