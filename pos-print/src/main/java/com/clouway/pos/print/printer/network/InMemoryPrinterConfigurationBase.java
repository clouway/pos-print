package com.clouway.pos.print.printer.network;

import com.clouway.pos.print.printer.Printer;
import com.google.common.collect.ImmutableMap;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class InMemoryPrinterConfigurationBase implements PrinterConfigurationBase{

  final ImmutableMap<String, NetworkPrinterConfiguration> CONFIGURATIONS =
          new ImmutableMap.Builder<String, NetworkPrinterConfiguration>()
                  .put("192.168.0.1", new NetworkPrinterConfiguration("192.168.0.1", 1024))
                  .put("192.168.0.2", new NetworkPrinterConfiguration("192.168.0.2", 1024))
                  .put("192.168.0.3", new NetworkPrinterConfiguration("192.168.0.3", 1024))
                  .put("85.217.129.107",new NetworkPrinterConfiguration("85.217.129.107", 1024))
                  .put("85.217.129.110",new NetworkPrinterConfiguration("85.217.129.107", 1024))   // lapostolovski
                  .put("85.217.129.111",new NetworkPrinterConfiguration("85.217.129.107", 1024))  // mlesikov    "10.10.24.31"   "10.10.24.32"
                  .put("85.217.129.112",new NetworkPrinterConfiguration("85.217.129.107", 1024))
                  .put("85.217.129.115",new NetworkPrinterConfiguration("85.217.129.107", 1024))    // mgenov
                  .put("85.217.129.121",new NetworkPrinterConfiguration("85.217.129.107", 1024))    // mgenov - home pc

                  .put("localhost",new NetworkPrinterConfiguration("85.217.129.107", 1024))
                  .put("127.0.0.1",new NetworkPrinterConfiguration("85.217.129.107", 1024))

                  // gorna
                  .put("85.217.131.21",new NetworkPrinterConfiguration("10.10.24.31", 1024))
                  .put("85.217.131.27",new NetworkPrinterConfiguration("10.10.24.32", 1024))

                  // pavlikeni
                  .put("85.217.191.153", new NetworkPrinterConfiguration("10.10.24.16",1024))

                  // elena
                  .put("85.217.191.157", new NetworkPrinterConfiguration("10.10.24.11",1024)) //old
                  .put("87.121.216.181", new NetworkPrinterConfiguration("10.10.24.11",1024)) //new 

                  // tyrnovo
                  .put("85.217.191.144", new NetworkPrinterConfiguration("10.10.24.13", 1024))
                  .put("85.217.191.154", new NetworkPrinterConfiguration("10.10.24.13", 1024))

                  // tyrnovo/pishmana
                  .put("85.217.191.179", new NetworkPrinterConfiguration("10.10.24.35", 1024))

                  // stragica
                  .put("85.217.130.34", new NetworkPrinterConfiguration("10.10.24.12", 1024))

                  // suhindol
                  .put("85.217.191.175", new NetworkPrinterConfiguration("10.10.24.39", 1024))

                  .build();

//  Офис ГО
//  ip address - 85.217.131.21
//  ip address - 85.217.131.27
//
//  Офис Павликени
//  ip address - 85.217.191.153
//
//  Офис Елена
//  ip address - 85.217.191.148
//
//
//  Офис Велико търново
//  ip address - 85.217.191.144
//  ip address - 85.217.191.154

//[10.10.24.12] rs-232-office-vt-01 - Касов апарат Велико Търново ( не се ползва в момента )
//
//[10.10.24.13] rs-232-office-vt-02 - Касов апарат Велико Търново
//
//[10.10.24.11] rs-232-office-elena - Касов апарат Елена
//
//[10.10.24.16] rs-232-office-pavlikeni - Касов апарат Павликени
//
//[10.10.24.27] rs-232-Strajica - Касов апарат Стражица
//
//[10.10.24.31] rs-232-office-go-01  - Касов апарат Горна Оряховица
//
//[10.10.24.32] rs-232-office-go-02  - Касов апарат Горна Оряховица

  public InMemoryPrinterConfigurationBase() {

  }

  /**
   * Gets printer configuration by providing the printer which configuration should be load.
   *  
   * @param printer the printer which configuration need to be retrieved
   * @return  the configuration of the provided printer
   */
  public NetworkPrinterConfiguration getPrinterConfiguration(Printer printer) {
    NetworkPrinterConfiguration configuration = CONFIGURATIONS.get(printer.getName());
    if (configuration == null) {
      return new NetworkPrinterConfiguration("127.0.0.1",1024);
    }
    return CONFIGURATIONS.get(printer.getName());
  }
}
