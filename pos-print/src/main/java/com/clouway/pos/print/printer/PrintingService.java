package com.clouway.pos.print.printer;

import com.clouway.pos.print.printer.network.NetworkPrintingStrategy;
import com.clouway.pos.print.printer.tremol.TremolPrintingModule;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * PrintingService is the entry point of the printing services. PrintingService provides the system with ability
 * to communicate with an external printer devices and to send different kind of messages to them.
 * <br/><br/>
 *
 * After PrintingModule was build then you are free to inject the main printing service {@link PrintService}. More information
 * about it's usage could be taken from {@link PrintService}
 *
 * TODO: Write some lines about network printers and how the printer communication is doing by the service classes.
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public final class PrintingService {

  
  public static PrintingModuleBuilder usingNetworkConnection() {
    final PrintingStrategy printingStrategy = new NetworkPrintingStrategy();
    return new PrintingModuleBuilder() {
      public Module buildModule() {
        return Modules.combine(printingStrategy.getBindings(), new TremolPrintingModule());
      }
    };
  }
}
