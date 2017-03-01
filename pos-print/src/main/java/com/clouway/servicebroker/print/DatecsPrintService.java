package com.clouway.servicebroker.print;

import com.clouway.servicebroker.print.internal.PrinterConnection;
import com.clouway.servicebroker.print.internal.PrinterConnector;
import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import com.google.inject.Inject;
import com.google.sitebricks.rendering.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class DatecsPrintService implements PrintService {
  private final Logger log = Logger.getLogger(DatecsPrintService.class.getName());


  public static final byte NEW_LINE = (byte) 0x2c;
  public static final byte OPEN_NON_FISCAL_BON = (byte) 0x26;
  public static final byte CLOSE_NON_FISCAL_BON = (byte) 0x27;
  public static final byte PRINT_TEXT_NON_FISCAL_BON = (byte) 0x2a;


  public static final byte OPEN_FISCAL_BON = (byte) 0x30;
  public static final byte PAYMENT = (byte) 0x31;
  public static final byte TOTAL = (byte) 0x35;
  public static final byte CLOSE_FISCAL_BON = (byte) 0x38;
  public static final byte PRINT_TEXT_FISCAL_BON = (byte) 0x36;

  public static final byte PRINT_DAILY_DEPARTAMENT_FINACIAL_REPORT = (byte) 0x75;

  public static final byte SHORT_REPORT_FROM_DATE_TO_DATE = (byte) 0x4f;
  public static final byte REPORT_FROM_DATE_TO_DATE = (byte) 0x5e;


  private final PrinterConnector connector;

  private byte seq = 0x20;
  private PrinterConnection printerConnection;

  @Inject
  public DatecsPrintService(PrinterConnector connector) {
    this.connector = connector;
  }


  public void connect() {
    log.info("Establish printer connection");
    // establish printer connection
    connector.openConnection();

    log.info("Open streams to that connection");
    // now we can open streams to that connection
    this.printerConnection = connector.getCurrentConnection();
  }

  public void disconnect() {
    connector.closeConnection();
  }

  public String getFiscalNumber() throws PrinterErrorException {
//    return printerBase.getFiscalNumber();
    return null;
  }

  public PrinterStatus getStatus() throws PrinterErrorException {
    return null;
  }


  public void printText(String text, TextAlign align) {
    try {
      sendCommand(PRINT_TEXT_NON_FISCAL_BON, fetchWordBytes(text), printerConnection);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Prints text to the printer  when fiscal bon is opened
   * Prints only  first 28 symbols, the other after the 28th are cut.
   *
   * @param text the text to be printed
   */
  public void printFiscalText(String text) {
    try {
      sendCommand(PRINT_TEXT_FISCAL_BON, fetchWordBytes(text), printerConnection);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void openBon() {
    try {
      sendCommand(OPEN_NON_FISCAL_BON, "".getBytes(), printerConnection);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  public void openFiscalBon() {
    try {
      closeFiscalBon();

      log.info("************* Opening new fiscal bon. *************");
//    sendCommand(OPEN_FISCAL_BON, openFiscalDetails("1", "10", "1"), printerConnection);
      //dp f550h  and  FP 2000 KL
      sendCommand(OPEN_FISCAL_BON, openFiscalDetails("1", "000000", "1").getBytes(), printerConnection);

      log.info("************* Fiscal bon was opened ************* ");

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void closeBon() {
    try {
      sendCommand(CLOSE_NON_FISCAL_BON, "".getBytes(), printerConnection);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  public void closeFiscalBon() {
    try {
      log.info("************* Closing fiscal bon ************* ");

      sendCommand(TOTAL, "".getBytes(), printerConnection);
      sendCommand(CLOSE_FISCAL_BON, "".getBytes(), printerConnection);

      log.info("************* Fiscal bon closed ************* ");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  public void lineFeed() {

  }

  public void sellFree(String name, float price, float quantity, float discount, String department) {
    log.info("*************  Item Payment ************* ");
    log.info("Quantity: " + quantity + ", Price: " + price + ", Discount: " + discount);
    try {
      sendCommand(PAYMENT, printableItem(name, price, quantity, "", 0f,department), printerConnection);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    log.info("************* Item payment completed. ************* ");

  }


  public void printLogo() {

  }

  public void printShortPeriodReport(Date start, Date end) {
    try {
      generateRandomSeq();
      sendCommand(SHORT_REPORT_FROM_DATE_TO_DATE, shortReportPeriod(start, end).getBytes(), printerConnection);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void printPeriodReport(Date start, Date end) {
      try {
      generateRandomSeq();
      sendCommand(REPORT_FROM_DATE_TO_DATE, shortReportPeriod(start, end).getBytes(), printerConnection);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private String shortReportPeriod(Date start, Date end) {
    SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMyy");
    String periodAsString = dateFormatter.format(start).toString()+","+dateFormatter.format(end).toString();
    return periodAsString;
  }


  public void printDepartmentDailyReport(boolean clearDailyTurnover) {
    try {
      generateRandomSeq();
      if (clearDailyTurnover) {
        //daily  report  by departaments    with clear
        sendCommand(PRINT_DAILY_DEPARTAMENT_FINACIAL_REPORT, "".getBytes(), printerConnection);
      } else {
        //daily  report  by departaments    with  NO clear
        sendCommand(PRINT_DAILY_DEPARTAMENT_FINACIAL_REPORT, "2".getBytes(), printerConnection);
      }

    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (InterruptedException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  private void generateRandomSeq() {
    seq += new Integer(new Random().nextInt(50)).byteValue();
  }

  public void sendCommand(final byte cmd, final byte[] data, final PrinterConnection printerConnection) throws IOException, InterruptedException {
    seq++;
    final OutputStream out = connector.getCurrentConnection().getOutputStream();
    final InputStream in = connector.getCurrentConnection().getInputStream();

    byte premable = 0x01;
    byte postamble = 0x05;
    byte terminator = 0x03;
    char separator = 0x04;
    char syn = 0x16;
    char nak = 0x15;

    byte len = (byte) (32 + 4 + data.length);
    byte[] dataBytes = data;


    //creating the command

    byte[] prefix = new byte[]{premable, len, seq, cmd};

    byte[] suffix = Bytes.concat(new byte[]{postamble}, calcBCC(dataBytes, seq, cmd), new byte[]{terminator});

    final byte[] request = Bytes.concat(prefix, dataBytes, suffix);

    String reqAsString = "";
    for (byte b : request) {
      reqAsString = reqAsString + Integer.toHexString(b & 0xff) + " ";
    }
    log.info("request :" + reqAsString);

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
//        for (byte b : response) {
//          log.info(Integer.toHexString(b) + " ");
//        }
//        log.info("i -> " + i);

        if (i > 12 && isValidResponse(response)) {
          translateResponse(readResponse(response));
          retry = false;
        } else {
          for (byte b : response) {
            if (b != nak) {

            } else if (b != syn) {
              throw new IllegalStateException(Integer.toHexString(b));
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
        Thread.sleep(60);
      } catch (Exception ee) {
      }
    }
  }

  private boolean isValidResponse(byte[] response) {
    List<String> statusResponse = readResponse(response);
    if (statusResponse.size() < 6) {
      System.out.println("NOT VALID RESPONSE READED");
      return false;
    }
    return true;
  }

  private List<String> readResponse(byte[] response) {
    log.info("RESPONSE READ :");
    List<String> statusResponse = new ArrayList<String>();
    boolean flag = false;
    int j = 0;
    for (int i = 0; i < response.length; i++) {
      byte tmp = response[i];
      if (tmp == 0x05) {
        flag = false;
      }
      if (flag) {
        statusResponse.add(Integer.toBinaryString(tmp).substring(24, 32));
        System.out.println("          " + statusResponse.get(j));
        j++;
      }
      if (tmp == 0x04) {
        flag = true;
      }
    }

    return statusResponse;
  }


  public void translateResponse(List<String> sb) throws IOException {

    if (sb.get(0).charAt(2) == '1') {
      System.out.println(" Fatal Error :\n");
    }
    if (sb.get(0).charAt(3) == '1') {
      System.out.println(" Mechanics Error ! (error with #)\n");
    }
    if (sb.get(0).charAt(6) == '1') {
      System.out.println(" # Invalid operation code(cmd) !\n");
    }
    if (sb.get(0).charAt(7) == '1') {
      System.out.println(" # DATA Syntax error !\n");
    }
    if (sb.get(1).charAt(3) == '1') {
      System.out.println(" MEMORY CORRUPT !\n");
    }
    if (sb.get(1).charAt(4) == '1') {
      System.out.println(" Print Canceled !\n");
    }        // NEVER REACHED ?
    if (sb.get(1).charAt(5) == '1') {
      System.out.println(" MEMORY Cleared !\n");
    }
    if (sb.get(1).charAt(6) == '1') {
      System.out.println(" # Command not allowed in current fiscal mode !\n");
    }
    if (sb.get(2).charAt(7) == '1') {
      System.out.println(" # NO PAPER !\n");
    } // should RETRY
    if (sb.get(4).charAt(2) == '1') {
      System.out.println(" MEMORY ERROR :\n");
    }
    if (sb.get(4).charAt(3) == '1') {
      System.out.println(" Memory FULL !\n");
    }
    if (sb.get(5).charAt(5) == '1') {
      System.out.println(" Unknown memory error !\n");
    }
    if (sb.get(5).charAt(7) == '1') {
      System.out.println(" Memory is READONLY !\n");
    }

//		if (sb[0].charAt(4) == '1') { System.out.println("Display pluged\n"); } else {System.out.println("No display plugged!\n");}
//		if (sb[0].charAt(5) == '1') { System.out.println("Clock is not set!\n"); } else {System.out.println("Clock is set\n");}
//		if (sb[1].charAt(7) == '1') { System.out.println("Cash Overflow ! Reduce !\n"); }
    if (sb.get(2).charAt(2) == '1') {
      System.out.println(" Nonfiscal BON opened !\n");
    }
    if (sb.get(2).charAt(4) == '1') {
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


  private byte[] printableItem(String itemDescription, float price, float quantity, String additionalItemDescription, float discountPercent, String department) throws UnsupportedEncodingException {
//    char tab = (char) 0x09;
//    char lf = (char) 0x0a;
//
//    if (Strings.empty(itemDescription)) {
//      throw new IllegalArgumentException("item description cannot be empty! ");
//    }
//
//    String printableItem = convertBytes(itemDescription);
//
//    if (!Strings.empty(additionalItemDescription)) {
//      printableItem += lf + convertBytes(additionalItemDescription);
//    }
//
////    do 500
//    final String department = "02";
//    printableItem += tab + department + tab;
//    //dp f550h
////    printableItem += tab + "A";
//
//    printableItem += price;
//
//    if (quantity > 1) {
//      printableItem += "*" + quantity;
//    }
//    if (discountPercent > -99.99d && discountPercent < 99.99d
//            && discountPercent != 0d) {
//      printableItem += "," + discountPercent;
//    } else if (discountPercent < -99.99d && discountPercent > 99.99d) {
//      throw new IllegalArgumentException("passed invalid discountPercent : " + discountPercent);
//    }
//    System.out.println("printableItem : " + printableItem);
////    return printableItem
//


    char tab = (char) 0x09;
    char lf = (char) 0x0a;

    if (Strings.empty(itemDescription)) {
      throw new IllegalArgumentException("item description cannot be empty! ");
    }
    List<Byte> bytes = Lists.newArrayList();

    byte[] data = new byte[0];


    bytes.addAll(Bytes.asList(fetchWordBytes(itemDescription)));

    if (!Strings.empty(additionalItemDescription)) {
      bytes.add((byte) 0x0a);
      bytes.addAll(Bytes.asList(fetchWordBytes(additionalItemDescription)));
    }

    String tail = tab + department + tab + price;
    //by group
//    String tail = tab + "Б" + price;

    if (quantity > 1) {
      tail += "*" + quantity;
    }

    if (discountPercent > -99.99d && discountPercent < 99.99d
            && discountPercent != 0d) {
      tail += "," + discountPercent;
    } else if (discountPercent < -99.99d && discountPercent > 99.99d) {
      throw new IllegalArgumentException("passed invalid discountPercent : " + discountPercent);
    }


    byte[] tailBytes = tail.getBytes("cp1251");

    for (int i = 0; i < tailBytes.length; i++) {
      byte tailByte = tailBytes[i];
      bytes.add((byte) (tailByte));
    }

    System.out.println("printableItem : " + new String(Bytes.toArray(bytes)));

    return Bytes.toArray(bytes);
  }
  //FP 2000 KL
  private byte[] fetchWordBytes(String word) throws UnsupportedEncodingException {
    byte[] data = word.getBytes("cp1251");
    return data;
  }
   ////dp f550h
  private byte[] fetchWordBytesWithCyrillicOffset(String word) throws UnsupportedEncodingException {
    byte[] data = word.getBytes("cp1251");
    for (int i = 0; i < data.length; i++) {
      if (isCyrillic(word.charAt(i))){
        data[i] = (byte) ((data[i] - 0x40) & 0xff);
      }else{
        data[i] = data[i];
      }
    }
    return data;
  }

  boolean isCyrillic(char c) {
    return Character.UnicodeBlock.CYRILLIC.equals(Character.UnicodeBlock.of(c));
  }


  boolean isCyrillic(byte c) {
    return ( 0x400 <= c && c <= 0x4ff );
  }

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

  private String openFiscalDetails(String operatorNumber, String operatorPassword, String department) {
    String openFiscalDetails = operatorNumber + "," + operatorPassword + "," + department;
    return openFiscalDetails;
  }


}
