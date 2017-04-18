package com.clouway.pos.print.printer;


import com.clouway.pos.print.core.Receipt;
import com.clouway.pos.print.core.ReceiptItem;
import com.clouway.pos.print.core.ReceiptPrinter;
import com.clouway.pos.print.core.RegisterState;
import com.clouway.pos.print.core.RequestTimeoutException;
import com.google.common.collect.Sets;
import com.google.common.primitives.Bytes;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * FP705 is a printer driver of Datecs's FP705 printer driver.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class FP705Printer implements ReceiptPrinter {

  private static final byte READ_DATE_TIME = (byte) 0x3E;

  private static final byte READ_STATUS_CMD = (byte) 0x4A;
  private static final byte SEQ_START = (byte) 0x20;

  /*
   * Fiscal Receipt Constants
   */
  private static final byte FISCAL_RECEIPT_OPEN = (byte) 0x30;
  private static final byte FISCAL_RECEIPT_CLOSE = (byte) 0x38;
  private static final byte FISCAL_RECEIPT_PAYMENT = (byte) 0x31;
  private static final byte FISCAL_RECEIPT_TOTAL = (byte) 0x35;
  private static final byte FISCAL_RECEIPT_PRINT_TEXT = (byte) 0x36;

  /**
   * Fiscal Memory Report by date
   */
  private static final byte FISCAL_MEMORY_REPORT_BY_DATE = (byte) 0x5E;

  /**
   * Report Operators
   */
  private static final byte REPORT_OPERATORS = (byte) 0x69;

  /*
   * Non Fiscal Receipt Constants
   */
  private static final byte TEXT_RECEIPT_OPEN = (byte) 0x26;
  private static final byte TEXT_RECEIPT_CLOSE = (byte) 0x27;
  private static final byte TEXT_RECEIPT_PRINT_TEXT = (byte) 0x2A;

  private static final byte SYN = 0x16;

  private InputStream inputStream;
  private OutputStream outputStream;

  public FP705Printer() {
  }

  public FP705Printer(InputStream inputStream, OutputStream outputStream) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  @Override
  public void printReceipt(Receipt receipt) throws IOException {
    byte seq = SEQ_START;

    Response response = sendPacket(buildPacket(seq++, TEXT_RECEIPT_OPEN, ""));
    printResponse(response);

    for (String prefix : receipt.prefixLines()) {
      printResponse(sendPacket(buildPacket(seq++, TEXT_RECEIPT_PRINT_TEXT, params(prefix))));
    }

    printResponse(sendPacket(buildPacket(seq++, TEXT_RECEIPT_PRINT_TEXT, params(""))));
    printResponse(sendPacket(buildPacket(seq++, TEXT_RECEIPT_PRINT_TEXT, params(""))));

    for (ReceiptItem item : receipt.getReceiptItems()) {
      String formattedRow = String.format("%s - %.2f X %.2f %s", item.getName(), item.getQuantity(), item.getPrice(), receipt.getCurrency());
      printResponse(sendPacket(buildPacket(seq++, TEXT_RECEIPT_PRINT_TEXT, params(formattedRow))));
    }

    printResponse(sendPacket(buildPacket(seq++, TEXT_RECEIPT_PRINT_TEXT, params(""))));
    printResponse(sendPacket(buildPacket(seq++, TEXT_RECEIPT_PRINT_TEXT, params(""))));

    for (String suffix : receipt.suffixLines()) {
      printResponse(sendPacket(buildPacket(seq++, TEXT_RECEIPT_PRINT_TEXT, params(suffix))));
    }
    
    printResponse(sendPacket(buildPacket(seq, TEXT_RECEIPT_CLOSE, "")));
  }


  public void reportForPeriod(LocalDate start, LocalDate end) throws IOException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

    String type = "0";
    String from = start.format(formatter);
    String to = end.format(formatter);
    String data = params(type, from, to);

    final int retryAttempts = 3;
    try {
      Response response = sendPacket(buildPacket(SEQ_START, FISCAL_MEMORY_REPORT_BY_DATE, data), retryAttempts);
      printResponse(response);
    } catch (RequestTimeoutException e) {
      // nop as  this operation is no returning anything
    }
  }

  /**
   * Gets report for a given operator by providing options for the preferred register
   * state after the operation.
   *
   * @param operatorId the id of the operator for which report need to be issued
   * @param state      the prefferred state after operation completes
   * @throws IOException is thrown in case of IO error
   */
  public void reportForOperator(String operatorId, RegisterState state) throws IOException {

    //Parameters of the command: {FirstOper}<SEP>{LastOper}<SEP>{Clear}<SEP> Optional parameters:
    // • FirstOper – First operator. Default: 1 (1÷30);
    // • LastOper – Last operator. Default: Maximum operator number (1÷30);
    // • Clear – Clear registers for operators. Default: 0;
    // • '0' – Does not clear registers for operators. '1' – Clear registers for operators.
    String clearRegister = "0";
    if (state == RegisterState.CLEAR) {
      clearRegister = "1";
    }
    final int retryAttempts = 3;
    
    try {

      sendPacket(buildPacket(SEQ_START, REPORT_OPERATORS, params(operatorId, operatorId, clearRegister)), retryAttempts);
    } catch (RequestTimeoutException e) {
      // nop as  this operation is no returning anything
    }

  }

  @Override
  public void printFiscalReceipt(Receipt receipt) throws IOException {
    byte seq = SEQ_START;

    printResponse(sendPacket(buildPacket(seq++, FISCAL_RECEIPT_OPEN, params("1", "0000", "1", ""))));

    for (String prefix : receipt.prefixLines()) {
      printResponse(sendPacket(buildPacket(seq++, FISCAL_RECEIPT_PRINT_TEXT, params(prefix))));
    }

    printResponse(sendPacket(buildPacket(seq++, FISCAL_RECEIPT_PRINT_TEXT, params(""))));

    double sum = 0;
    for (ReceiptItem item : receipt.getReceiptItems()) {
      String priceValue = String.format("%.2f", item.getPrice());
      String quantityValue = String.format("%.3f", item.getQuantity());
      printResponse(sendPacket(buildPacket(seq++, FISCAL_RECEIPT_PAYMENT, params(item.getName(), "1", priceValue, quantityValue, "0", "", "0"))));
      sum += item.getPrice() * item.getQuantity();
    }

    printResponse(sendPacket(buildPacket(seq++, FISCAL_RECEIPT_PRINT_TEXT, params(""))));

    for (String suffix : receipt.suffixLines()) {
      printResponse(sendPacket(buildPacket(seq++, FISCAL_RECEIPT_PRINT_TEXT, params(suffix))));
    }

    printResponse(sendPacket(buildPacket(seq, FISCAL_RECEIPT_TOTAL, params("0", String.format("%.2f", sum)))));
    printResponse(sendPacket(buildPacket(seq, FISCAL_RECEIPT_CLOSE, "")));
  }

  @Override
  public void close() throws IOException {
    outputStream.close();
  }

  /**
   * Gets status of the printer. If printer has errors the result be a list of errors which are
   * encountered. If returned list of empty then the result is OK for the cash register.
   *
   * @throws IOException           is thrown in case of communication error with the cash register
   * @throws IllegalStateException is thrown in case when
   */
  public Set<String> getStatus() throws IOException {
    Response response = sendPacket(buildPacket(SEQ_START, READ_STATUS_CMD, ""));
    return decodeStatus(response.status());
  }

  /**
   * Gets current time of the fiscal device.
   *
   * @return the time of the fiscal device
   * @throws IOException           is thrown in case when device cannot be communicated due IO error.
   * @throws IllegalStateException is thrown if
   */
  public String getTime() throws IOException {
    Response response = sendPacket(buildPacket(SEQ_START, READ_DATE_TIME, ""));
    printResponse(response);
    return new String(response.data());
  }

  private void printResponse(Response response) {
    System.out.println("Response: ");
    for (byte b : response.raw()) {
      System.out.print(String.format("0x%02X, ", b));
    }
    System.out.println();
    System.out.printf("Data (%d): ", response.data().length);
    for (byte b : response.data()) {
      System.out.printf("0x%02X ", b);
    }
    System.out.println();
    
    System.out.println("Status: " + decodeStatus(response.status()));
  }

  private byte[] buildPacket(final byte seq, final byte cmd, String data) {
    byte premable = 0x01;
    byte postamble = 0x05;
    byte terminator = 0x03;

    byte[] dataBytes = new byte[0];
    try {
      dataBytes = data.getBytes("cp1251");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    int len = 32 + 1 + 4 + 4 + dataBytes.length + 1;

    byte l1 = (byte) (0x30 + ((len) & 0x0f));
    byte l2 = (byte) (0x30 + ((len >> 4) & 0x0f));
    byte l3 = (byte) (0x30 + ((len >> 8) & 0x0f));
    byte l4 = (byte) (0x30 + ((len >> 12) & 0x0f));


    byte c1 = (byte) (0x30 + ((cmd) & 0x0f));
    byte c2 = (byte) (0x30 + ((cmd >> 4) & 0x0f));
    byte c3 = (byte) (0x30 + ((cmd >> 8) & 0x0f));
    byte c4 = (byte) (0x30 + ((cmd >> 12) & 0x0f));

    //creating the command
    byte[] prefix = new byte[]{premable, l4, l3, l2, l1, seq, c4, c3, c2, c1};

    byte[] suffix = Bytes.concat(new byte[]{postamble}, calcBCC(dataBytes, seq, cmd), new byte[]{terminator});

    return Bytes.concat(prefix, dataBytes, suffix);
  }

  private Response sendPacket(byte[] request) throws IOException {
    final int maxRetries = 10;
    return sendPacket(request, maxRetries);
  }

  private Response sendPacket(byte[] request, int maxRetries) throws IOException {
    BufferedSink sink = Okio.buffer(Okio.sink(outputStream));

    System.out.println("========================");
    System.out.print("Request: ");
    for (byte b : request) {
      System.out.printf("0x%02X, ", b);
    }
    
    sink.write(request);
    sink.flush();

    BufferedSource source = Okio.buffer(Okio.source(inputStream));

    Buffer buffer = new Buffer();
    for (int retry = 0; retry < maxRetries; retry++) {
      try {
        source.read(buffer, 2048);

        if (buffer.indexOf(SYN) > 0) {
          System.out.println("Got Sync");
        }

        long from = buffer.indexOf((byte) 0x01);
        long to = buffer.indexOf((byte) 0x03);

        if (from >= 0 & to > 0) {
          buffer.skip(from);
          byte[] content = buffer.readByteArray(to - from + 1);

          return new FP705Response(content);
        }

        Thread.sleep(200);

      } catch (InterruptedException | SocketTimeoutException e) {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    }

    throw new RequestTimeoutException(String.format("unable to get response after %d retries", maxRetries));
  }

  private byte[] calcBCC(byte[] data, byte seq, byte cmd) {
    int bcc = 0;

    int lng = 32 + 1 + 4 + 4 + data.length + 1;
    byte l1 = (byte) (0x30 + ((lng) & 0x0f));
    byte l2 = (byte) (0x30 + ((lng >> 4) & 0x0f));
    byte l3 = (byte) (0x30 + ((lng >> 8) & 0x0f));
    byte l4 = (byte) (0x30 + ((lng >> 12) & 0x0f));

    bcc += l1;
    bcc += l2;
    bcc += l3;
    bcc += l4;

    byte c1 = (byte) (0x30 + ((cmd) & 0x0f));
    byte c2 = (byte) (0x30 + ((cmd >> 4) & 0x0f));
    byte c3 = (byte) (0x30 + ((cmd >> 8) & 0x0f));
    byte c4 = (byte) (0x30 + ((cmd >> 12) & 0x0f));

    bcc += seq;

    bcc += c4;
    bcc += c3;
    bcc += c2;
    bcc += c1;

    for (byte b : data) {
      bcc += b & 0xff;
    }

    bcc += 0x05;

    byte b1 = (byte) (0x30 + ((bcc) & 0x0f));
    byte b2 = (byte) (0x30 + ((bcc >> 4) & 0x0f));
    byte b3 = (byte) (0x30 + ((bcc >> 8) & 0x0f));
    byte b4 = (byte) (0x30 + ((bcc >> 12) & 0x0f));

    return new byte[]{b4, b3, b2, b1};
  }

  private String params(Object... param) {
    StringBuilder params = new StringBuilder();
    for (Object each : param) {
      params.append(each);
      params.append("\t");
    }
    return params.toString();
  }

  private Set<String> decodeStatus(byte[] status) {

    Set<String> result = Sets.newLinkedHashSet();

    for (Status each : Status.values()) {
      if (each.isSetIn(status)) {

        result.add(each.name());
      }
    }

    return result;
  }

}
