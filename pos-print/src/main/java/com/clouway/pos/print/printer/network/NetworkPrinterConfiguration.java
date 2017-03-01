package com.clouway.pos.print.printer.network;

/**
 * NetworkPrinterConfiguration is a configuration item that is holding information that is required
 * by the network printers.
 * 
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class NetworkPrinterConfiguration {
    private final String ip;
    private final Integer port;

    public NetworkPrinterConfiguration(String ip, Integer port) {
      this.ip = ip;
      this.port = port;
    }

    public String getIp() {
      return ip;
    }

    public Integer getPort() {
      return port;
    }
}
