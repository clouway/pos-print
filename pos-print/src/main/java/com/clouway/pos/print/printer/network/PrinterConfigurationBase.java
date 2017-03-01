package com.clouway.pos.print.printer.network;


import com.clouway.pos.print.printer.Printer;

/**
 * PrinterConfigurationBase is a base class for retrieving printer configuration
 * @author Miroslav Genov (mgenov@gmail.com)
 */
interface PrinterConfigurationBase {

  /**
   * Gets the configuration of a given printer.
   * @param printer the printer which configuration need to be retrieved
   * @return the network configuration of the provided printer.
   */
  NetworkPrinterConfiguration getPrinterConfiguration(Printer printer);
  
}
