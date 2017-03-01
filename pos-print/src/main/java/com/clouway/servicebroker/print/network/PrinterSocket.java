package com.clouway.servicebroker.print.network;

import com.clouway.servicebroker.print.CommunicationErrorException;
import com.clouway.servicebroker.print.PrinterCommunicationError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * PrinterSocket is a wrapper class over java.util.Socket that is handling in a proper way IOException's that are thrown by the socket
 * when connection is established or when connection streams are retrieved from it.
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class PrinterSocket {
  private Logger log = Logger.getLogger(PrinterSocket.class.getName());

  private final String host;
  private final int port;

  private Socket socket;

  public PrinterSocket(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public PrinterSocket connect() {
    try {
      log.info("Connecting to: " + host + " using port " + port);
      socket = new Socket(host, port);
    } catch (IOException e) {
      log.info("Cant open socket! Cached IOExceptions and throw CommunicationErrorException!");
      throw new CommunicationErrorException(PrinterCommunicationError.connectionCannotBeOpened());
    }
    
    return this;
  }

  public InputStream getInputStream() {
    try {
      return socket.getInputStream();
    } catch (IOException e) {
      log.info("Get input stream from socket throw IOException!");
      throw new CommunicationErrorException(PrinterCommunicationError.brokenCommunication());
    }
  }

  public OutputStream getOutputStream() {
    try {
      return socket.getOutputStream();
    } catch (IOException e) {
      log.info("Get output stream from socket throw IOException!");
      throw new CommunicationErrorException(PrinterCommunicationError.brokenCommunication());
    }
  }

  public void close() {
    if (socket.isClosed()) {
      throw new IllegalStateException("Socket already closed.");
    }
    try {
      socket.close();
    } catch (IOException e) {
      log.info("Closing socket throw IOException!");
      throw new CommunicationErrorException(PrinterCommunicationError.connectionCannotBeClosed());
    }
  }
}
