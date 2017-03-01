package com.clouway.pos.print.printer.datecs;

import com.clouway.pos.print.printer.network.PrinterSocket;
import com.google.common.base.Strings;
import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class DatecsPrinterBase {
  private static final Logger log = Logger.getLogger(DatecsPrinterBase.class.getName());

  public static final byte NEW_LINE = (byte) 0x2c;
  public static final byte OPEN_NON_FISCAL_BON = (byte) 0x26;
  public static final byte CLOSE_NON_FISCAL_BON = (byte) 0x27;
  public static final byte PRINT_TEXT_NON_FISCAL_BON = (byte) 0x2a;


  public static final byte OPEN_FISCAL_BON = (byte) 0x30;
  public static final byte PAYMENT = (byte) 0x31;
  public static final byte TOTAL = (byte) 0x35;
  public static final byte CLOSE_FISCAL_BON = (byte) 0x38;
  public static final byte PRINT_TEXT_FISCAL_BON = (byte) 0x36;



  final String DEFAULT_IP = "85.217.129.119";
  final Integer DEFAULT_PORT = 1024;


  public byte[] calcBCC(byte[] data, byte seq, byte cmd) {
    short bcc = 0;
    bcc += seq;   //   seq
    bcc += cmd;   //  cmd
    int lng = 32 + 4 + data.length;
    bcc += lng;
    for (int i = 0; i < data.length; i++) {
      bcc += (data[i] & 0x000000ff);
    }
    bcc += 0x05;
    byte b1 = (byte) (((((byte) (bcc / 256)) >> 4) & 0x0F) + '0');
    byte b2 = (byte) (((((byte) (bcc / 256))) & 0x0F) + '0');
    byte b3 = (byte) (((((byte) (bcc % 256)) >> 4) & 0x0F) + '0');
    byte b4 = (byte) (((((byte) (bcc % 256))) & 0x0F) + '0');

    return new byte[]{b1, b2, b3, b4};
  }



  //    @Test
//  public void testPrintingNONFiscalBon() throws Exception {
//    PrinterSocket printerSocket = new PrinterSocket(DEFAULT_IP, DEFAULT_PORT);
//    printerSocket.connect();
//    byte seq = 0x21;
//
//    sendCommand(seq++, OPEN_NON_FISCAL_BON, "", printerSocket);
////
//    for (int i = 0; i < 40; i++) {
//      sendCommand(seq++, PRINT_TEXT_NON_FISCAL_BON, i + "some test text", printerSocket);
//      sendCommand(seq++, PRINT_TEXT_NON_FISCAL_BON, i + "малко КИРИЛИЦА", printerSocket);
//    }
//
//    sendCommand(seq++, CLOSE_NON_FISCAL_BON, "", printerSocket);
//
//    sendCommand(seq++, NEW_LINE, "3", printerSocket);
//
//    printerSocket.close();
//  }

//

//
//  @Test
//  public void testPrintingFiscalBon() throws Exception {
//    PrinterSocket printerSocket = new PrinterSocket(DEFAULT_IP, DEFAULT_PORT);
//    printerSocket.connect();
//    byte seq = 0x21;
//
////    do 500
//    sendCommand(seq++, OPEN_FISCAL_BON, openFiscalDetails("1", "10", "1"), printerSocket);
//    //dp f550h
////    sendCommand(seq++, OPEN_FISCAL_BON, openFiscalDetails("1", "000000", "1"), printerSocket);
//
//    for (int i = 1; i <= 10; i++) {
//      if (i % 2 == 0) {
//        sendCommand(seq++, PAYMENT, printableItem("тестов продукт" + i, -0.01f, 1f, "НАЕМ", 0f), printerSocket);
//      } else {
//        sendCommand(seq++, PAYMENT, printableItem("testing product" + i, 0.1f, 2f, "SERVICE", -50f), printerSocket);
//      }
//
//    }
//
//    sendCommand(seq++, TOTAL, "", printerSocket);
//    sendCommand(seq++, CLOSE_FISCAL_BON, "", printerSocket);
//
//    printerSocket.close();
//
//  }

  private String openFiscalDetails(String operatorNumber, String operatorPassword, String department) {
    String openFiscalDetails = operatorNumber + "," + operatorPassword + "," + department;
    return openFiscalDetails;
  }

  private String printableItem(String itemDescription, float price, float quantity, String additionalItemDescription, float discountPercent) {
    char tab = (char) 0x09;
    char lf = (char) 0x0a;

    if (Strings.isNullOrEmpty(itemDescription)) {
      throw new IllegalArgumentException("item description cannot be empty! ");
    }

    String printableItem = itemDescription;

    if (!Strings.isNullOrEmpty(additionalItemDescription)) {
      printableItem += lf + additionalItemDescription;
    }

//    do 500
    printableItem += tab + "01" + tab;
    //dp f550h
//    printableItem += tab + "A";

    printableItem += price;

    if (quantity > 1) {
      printableItem += "*" + quantity;
    }
    if (discountPercent > -99.99d && discountPercent < 99.99d
            && discountPercent != 0d) {
      printableItem += "," + discountPercent;
    } else if (discountPercent < -99.99d && discountPercent > 99.99d) {
      throw new IllegalArgumentException("passed invalid discountPercent : " + discountPercent);
    }
    System.out.println("printableItem : " + printableItem);
    return printableItem;
  }


  public void sendCommand(final byte seq, final byte cmd, final String data, final PrinterSocket socket) throws IOException, InterruptedException {

    final OutputStream out = socket.getOutputStream();
    InputStream in = socket.getInputStream();

    byte premable = 0x01;
    byte postamble = 0x05;
    byte terminator = 0x03;
    char separator = 0x04;
    char nak = 0x15;
    char syn = 0x16;

    byte len = (byte) (32 + 4 + data.length());
    byte[] dataBytes = data.getBytes("Cp1251");


    //creating the command
    byte[] prefix = new byte[]{premable, len, seq, cmd};

    byte[] suffix = Bytes.concat(new byte[]{postamble}, calcBCC(dataBytes, seq, cmd), new byte[]{terminator});

    final byte[] request = Bytes.concat(prefix, dataBytes, suffix);

    System.out.println("request :");
    for (byte b : request) {
      System.out.print(Integer.toHexString(b) + " ");
    }
    System.out.println();

    out.write(request);
    out.flush();


    boolean retry = true;
    long start = System.currentTimeMillis();
    while (retry) {
      if (in.available() > 0) {
        int length = in.available();
        byte[] response = new byte[length];
        int i = in.read(response, 0, length);

        //analyzing response
        for (byte b : response) {
          System.out.print(Integer.toHexString(b) + " ");
        }
        System.out.println("i -> " + i);

        if (i > 12) {
          readResponse(response);
          retry = false;
        }else {
          for (byte b : response) {
            if(b != nak){

            }else if(b!=syn){
                throw new IllegalStateException("Illegal Printer Response was received: " + Integer.toHexString(b));
             }
          }
        }
      } else if (System.currentTimeMillis() - start > 1000) {
        start = System.currentTimeMillis();
        out.write(request);
        out.flush();
        log.info("request timeout: RETRY");
      }

      // waiting for printer response
      try {
        Thread.sleep(30);
      } catch (Exception ee) {
      }
    }
  }



  public void readResponse(byte[] response) throws IOException {
    log.info("response :");

    boolean flag = false;
    String[] sb = new String[6];
    int j = 0;
    for (int i = 0; i < response.length; i++) {
      byte tmp = response[i];
      if (tmp == 0x05) flag = false;
      if (flag) {
        sb[j] = Integer.toBinaryString(tmp).substring(24, 32);
        System.out.println("          " + sb[j]);
        j++;
      }
      if (tmp == 0x04) {
        flag = true;
      }
    }

    if (sb[0].charAt(2) == '1') {System.out.println(" Fatal Error :\n");}
    if (sb[0].charAt(3) == '1') {System.out.println(" Mechanics Error ! (error with #)\n");}
    if (sb[0].charAt(6) == '1') {System.out.println(" # Invalid operation code(cmd) !\n");}
    if (sb[0].charAt(7) == '1') {System.out.println(" # DATA Syntax error !\n");}
    if (sb[1].charAt(3) == '1') {System.out.println(" MEMORY CORRUPT !\n");}
    if (sb[1].charAt(4) == '1') {System.out.println(" Print Canceled !\n");}        // NEVER REACHED ?
    if (sb[1].charAt(5) == '1') {System.out.println(" MEMORY Cleared !\n");}
    if (sb[1].charAt(6) == '1') {System.out.println(" # Command not allowed in current fiscal mode !\n");}
    if (sb[2].charAt(7) == '1') {System.out.println(" # NO PAPER !\n");} // should RETRY
    if (sb[4].charAt(2) == '1') {System.out.println(" MEMORY ERROR :\n");}
    if (sb[4].charAt(3) == '1') {System.out.println(" Memory FULL !\n");}
    if (sb[5].charAt(5) == '1') {System.out.println(" Unknown memory error !\n");}
    if (sb[5].charAt(7) == '1') {System.out.println(" Memory is READONLY !\n");}

//		if (sb[0].charAt(4) == '1') { System.out.println("Display pluged\n"); } else {System.out.println("No display plugged!\n");}
//		if (sb[0].charAt(5) == '1') { System.out.println("Clock is not set!\n"); } else {System.out.println("Clock is set\n");}
//		if (sb[1].charAt(7) == '1') { System.out.println("Cash Overflow ! Reduce !\n"); }
    if (sb[2].charAt(2) == '1') {
      System.out.println(" Nonfiscal BON opened !\n");
    }
    if (sb[2].charAt(4) == '1') {
      System.out.println(" Fiscal BON opened !\n");
    }
//		if (sb[3].charAt(1) == '1') { System.out.println("Switch 2.2 is ON\n"); } else {System.out.println("Switch 2.2 is OFF\n");}
//		if (sb[3].charAt(2) == '1') { System.out.println("Switch 2.1 is ON\n"); } else {System.out.println("Switch 2.2 is OFF\n");}
//		if (sb[3].charAt(3) == '1') { System.out.println("Switch 1.5 is ON\n"); } else {System.out.println("Switch 1.5 is OFF\n");}
//		if (sb[3].charAt(4) == '1') { System.out.println("Switch 1.4 is ON\n"); } else {System.out.println("Switch 1.4 is OFF\n");}
//		if (sb[3].charAt(5) == '1') { System.out.println("Switch 1.3 is ON\n"); } else {System.out.println("Switch 1.3 is OFF\n");}
//		if (sb[3].charAt(6) == '1') { System.out.println("Switch 1.2 is ON\n"); } else {System.out.println("Switch 1.2 is OFF\n");}
//		if (sb[3].charAt(7) == '1') { System.out.println("Switch 1.1 is ON\n"); } else {System.out.println("Switch 1.1 is OFF\n");}
//		if (sb[4].charAt(4) == '1') { System.out.println("Less than 50 units of memory remain !\n"); }
//		if (sb[4].charAt(5) == '1') { System.out.println("NO FISCAL MEMORY !\n"); }
//		if (sb[4].charAt(7) == '1') { System.out.println("Memory write error !\n"); }
//		if (sb[5].charAt(2) == '1') { System.out.println("Memory and fiscal data set !\n"); } else { System.out.println("Memory and fiscal data are NOT set !\n"); }
//		if (sb[5].charAt(3) == '1') { System.out.println("Tax groups set !\n"); } else { System.out.println("Tax groups are NOT set !\n"); }
//		if (sb[5].charAt(4) == '1') { System.out.println("Printer is in fiscal mode !\n"); } else { System.out.println("Printer is NOT in fiscal mode !\n"); }
//		if (sb[5].charAt(6) == '1') { System.out.println("Fiscal memory is cleared !\n"); }

  }

}
