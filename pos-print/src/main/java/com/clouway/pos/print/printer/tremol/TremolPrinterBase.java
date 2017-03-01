package com.clouway.pos.print.printer.tremol;

import com.clouway.pos.print.printer.*;
import com.google.inject.Provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class TremolPrinterBase implements PrinterBase {
  private final Logger log = Logger.getLogger(TremolPrinterBase.class.getName());

  public static final int ZFP_TEXTALIGNLEFT = 0;

  public static final int ZFP_TEXTALIGNRIGHT = 1;

  public static final int ZFP_TEXTALIGNCENTER = 2;

  private static int m_lastNbl = 0x20;

  private static int m_lang = 1;

  private static byte[] m_receiveBuf = new byte[256];
  private static int m_receiveLen;
  private InputStream in;
  private OutputStream out;
  private final Provider<PrinterConnection> connection;


  public TremolPrinterBase(Provider<PrinterConnection> connection) {
    this.connection = connection;
  }

  public void openStreams() {
    PrinterConnection connection = this.connection.get();
    this.in = connection.getInputStream();
    this.out = connection.getOutputStream();
  }


  /**
   * Gets Zeka FP Tax Memory number
   *
   * @return Tax Memory number - string 10 characters long
   * @throws ZFPException in case of communication error
   */
  public String getFiscalNumber() {
    try {
      sendCommand((byte) 0x60, null);
    } catch (Exception e) {
      throw new PrinterErrorException(PrinterError.fiscalNumber());
    }
    return new String(m_receiveBuf, 13, 8).trim();
  }


  public PrinterStatus getStatus() {
    try {
      sendCommand((byte) 0x20, null);
      return new PrinterStatus(m_receiveBuf, m_receiveLen, ZFPException.ZFP_LANG_BG);
    } catch (Exception e) {
      throw new PrinterErrorException(PrinterError.readStatus());
    }
  }

  private void getResponse() throws ZFPException {
    int read;

    long start = System.currentTimeMillis();

    do {
      try {
        if (0 < in.available()) {
          read = in.read(m_receiveBuf, 0, 1);
          if (0 < read) {
            if ((byte) 0x06 == m_receiveBuf[0]) {  // ACK
              break;
            } else if ((byte) 0x02 == m_receiveBuf[0]) { // STX
              break;
            } else if ((byte) 0x15 == m_receiveBuf[0]) { // NACK
              throw new ZFPException(0x103, m_lang);
            } else if ((byte) 0x03 == m_receiveBuf[0]) { // ANTIECHO
              throw new ZFPException(0x10E, m_lang);
            } else if ((byte) 0x0E == m_receiveBuf[0]) { // RETRY
              // ToDo
              break;
            }
          }
        }
      }
      catch (Exception e) {
        throw new ZFPException(e);
      }

      if (g_timeout < System.currentTimeMillis() - start) {
        throw new ZFPException(0x102, m_lang);
      }

      try {
        if (0 == in.available())
          wait(20);
      }
      catch (Exception e) {
      }
    } while (true);

    // read the data
    m_receiveLen = 1;
    int avail;
    do {
      try {
        avail = in.available();
      }
      catch (Exception e) {
        throw new ZFPException(e);
      }
      if (0 < avail) {
        try {
          read = in.read(m_receiveBuf, m_receiveLen, 1);
        }
        catch (Exception e) {
          throw new ZFPException(e);
        }
        if (0 < read) {
          if ((byte) 0x0A == m_receiveBuf[m_receiveLen]) {
            m_receiveLen += read;
            break;
          }
          m_receiveLen += read;
        }
      }
      // timeout check
      if (g_timeout < System.currentTimeMillis() - start) {
        throw new ZFPException(0x102, m_lang);
      }
      try {
        wait(20);
      }
      catch (Exception e) {
      }
    } while (true);


    if (!makeCRC(m_receiveBuf, m_receiveLen, 1)) {
      throw new ZFPException(0x104, m_lang);
    }

    if ((byte) 0x06 == m_receiveBuf[0]) {  // ACK
      if (((byte) 0x30 != m_receiveBuf[2]) || ((byte) 0x30 != m_receiveBuf[3])) {
        log.info("m_receiveBuf: " + m_receiveBuf);
        String number = new String(m_receiveBuf, 3, 2);
        log.info("m_receiveBuf STRING RETURNED: " + number);
            
        number = number.replace(':', ' ');
        number = number.replace(';', ' ');
        number = number.replace('?', ' ');
        number = number.replace('>', ' ');
        number = number.replace('<', ' ');
        number = number.replace('=', ' ');
        number = number.trim();
        int error = Integer.parseInt(number, 16);
        throw new ZFPException(error, m_lang);
      }
    } else if (m_receiveBuf[2] != (byte) m_lastNbl) {
      throw new ZFPException(0x10B, m_lang);
    }
  }


  public void payment(float sum, int type, boolean noRest) {
    if ((0 > type) || (4 < type) || (0.0f > sum) || (9999999999.0f < sum))
      throw new IllegalArgumentException("Invalid payment arguments where provided.");

    String data = Integer.toString(type);
    data += noRest ? ";1;" : ";0;";
    data += getFloatFormat(sum, 2);
    log.info("Payment Data:" + data);
    try {
      sendCommand((byte) 0x35, data.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
      throw new PrinterErrorException(PrinterError.payment());
    }
  }

  public void printText(String text, TextAlign align) {
    try {
      int textAlign = align.getValue();
      final Integer textLength = 34;
      if (34 <= text.length())
        textAlign = ZFP_TEXTALIGNLEFT;

      String data;
      switch (textAlign) {
        case ZFP_TEXTALIGNRIGHT:
          data = new PrintfFormat("%" + (textLength - 4) + "s").sprintf(nstrcpy(text, textLength - 4));
          break;

        case ZFP_TEXTALIGNCENTER: {
          StringBuffer buf = new StringBuffer("                                  "); // 34 spaces
          int pos = (textLength - text.length()) / 2;
          data = buf.replace(pos, pos + text.length(), text).toString();
        }
        break;

        default:
          data = new PrintfFormat("%-" + textLength + "s").sprintf(nstrcpy(text, textLength));
          break;
      }

      sendCommand((byte) 0x37, data.getBytes("cp1251"));
    } catch (UnsupportedEncodingException e) {
      throw new PrinterErrorException(PrinterError.invalidTextEncoding());
    } catch (PrinterErrorException e) {
      throw e;
    } catch (Exception e) {
      throw new PrinterErrorException(PrinterError.printText());
    }

  }

  public void openBon(int oper, String pass) {
    if ((9 < oper) || (1 > oper))
      throw new IllegalArgumentException("Illegal oper or pass.");

    String data = Integer.toString(oper);
    data += ";";
    data += new PrintfFormat("%-4s").sprintf(nstrcpy(pass, 4));

    try {
      sendCommand((byte) 0x2E, data.getBytes());

    } catch (PrinterErrorException e) {
      if (e.containsError(PrinterError.bonAlreadyOpened())) {
        closeBon();
        sendCommand((byte) 0x2E, data.getBytes());
      }
    } catch (Exception e) {
      throw new PrinterErrorException(PrinterError.openBon());
    }
  }

  /**
   * Closes the opened non client receipt
   */
  public void closeBon() {
    try {
      sendCommand((byte) 0x2F, null);
    } catch (PrinterErrorException e) {
      e.addError(PrinterError.closeBon());
      throw e;
    }
  }

  public void lineFeed() {
    try {
      sendCommand((byte) 0x2B, null);
    } catch (Exception e) {
      throw new PrinterErrorException(PrinterError.lineFeed());
    }
  }


  private void sendCommand(byte cmd, byte[] data) {
    try {
      checkForZFP();
      checkForZFPBusy();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Send command error!", e);
      throw new PrinterErrorException(PrinterError.deviceError());
    }

    // prepare the command
    int len = (null != data) ? data.length : 0;
    byte[] fullCmd = new byte[4 + len + 3];
    fullCmd[0] = (byte) 0x02;                // STX
    fullCmd[1] = (byte) (len + 0x20 + 0x03); // LEN
    if (0xFF < ++m_lastNbl)
      m_lastNbl = 0x20;
    fullCmd[2] = (byte) m_lastNbl;           // NBL
    fullCmd[3] = cmd;                       // CMD

    if (null != data)
      System.arraycopy(data, 0, fullCmd, 4, len);

    makeCRC(fullCmd, fullCmd.length, 0);
    fullCmd[fullCmd.length - 1] = (byte) 0x0A; // ETX

    for (byte b : fullCmd) {
      System.out.print((char) b);
    }

    try {
      out.write(fullCmd);
      out.flush();
    } catch (IOException e) {
      throw new CommunicationErrorException(PrinterCommunicationError.brokenCommunication());
    }

    try {
      getResponse();
    } catch (ZFPException e) {
      if (e.getMessage().equals(" (0022 hex) Ф.П.: препълване в общите регистри / команда: непозволена!")) {
        throw new PrinterErrorException(PrinterError.bonAlreadyOpened());
      }
      e.printStackTrace();
      // not so sure where this is a device error cause invalid commands could be sent to the device
      throw new PrinterErrorException(PrinterError.deviceError());
    }
  }

  /**
   * Calculates the sub total sum of the receipt
   *
   * @param print     flag for print the sub total sum
   * @param show      flag for show the sub total sum on the external display
   * @param isPercent flag for percentage discount/addition
   * @param discount  discount/addition value
   * @param taxgrp    specifies the tax group - ignored in Bulgarian FP version
   * @return returns the sub total sum
   * @throws ZFPException if the input parameters are incorrect or in case of communication error
   */
  public float calcIntermediateSum(boolean print, boolean show, boolean isPercent,
                                   float discount, char taxgrp) {
    StringBuffer data = new StringBuffer();
    data.append(print ? '1' : '0');
    data.append(";");
    data.append(show ? '1' : '0');
    if (0.0f != discount) {
      if (isPercent) {
        data.append(",");
        data.append(new PrintfFormat("%6.2f").sprintf(discount));
        data.append("%");
      } else {
        data.append(":");
        data.append(getFloatFormat(discount, 2));
      }
    }

    try {
      sendCommand((byte) 0x33, data.toString().getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }

    log.info(new String(m_receiveBuf, 4, m_receiveLen));

    String sumResponse = new String(m_receiveBuf, 4, m_receiveLen - 7).trim();
    log.info("Sum Response:" + sumResponse);
    return Float.parseFloat(sumResponse);
  }

  private boolean makeCRC(byte[] data, int len, int mode) {
    // calculate the CRC
    byte crc = 0;
    for (int i = 1; i < len - 3; i++)
      crc ^= data[i];

    switch (mode) {
      case 0:
        // add the CRC
        data[len - 3] = (byte) ((crc >> 4) | 0x30);
        data[len - 2] = (byte) ((crc & 0x0F) | 0x30);
        break;

      case 1:
        // add the CRC
        byte test = (byte) (((crc >> 4) & 0x0F) | 0x30);
        test |= (byte) 0x30;
        if (data[len - 3] != (byte) (((crc >> 4) & 0x0F) | 0x30))
          return false;
        if (data[len - 2] != (byte) ((crc & 0x0F) | 0x30))
          return false;
        break;
    }
    return true;
  }

//  protected static final long g_timeout = 150;

  protected static final long g_timeout = 1000;

  private boolean doPing(InputStream m_inputStream, OutputStream m_outputStream, byte ping, int retries) throws Exception {
    byte[] b = new byte[1];
    for (int i = 0; i < retries; i++) {
      try {

        b[0] = (byte) 0x03;  // antiecho

        m_outputStream.write(b);

        b[0] = ping;        // ping
        m_outputStream.write(b);

        b[0] = 0;
        long start = System.currentTimeMillis();
        do {
          if (0 < m_inputStream.available()) {
            m_inputStream.read(b);

            if (b[0] == (byte) 0x03) {
              throw new Exception();
            }
            if (b[0] == ping) {
              return true;
            }


            Thread.sleep(10);


          }
        } while (g_timeout > System.currentTimeMillis() - start);
      }
      catch (Exception e) {
        log.log(Level.SEVERE, "Do ping exception!", e);
        throw new Exception(e);
      }
    }
    throw new Exception();
  }

  public void sellFree(String name, char taxgrp, float price, float quantity, float discount) {
    if ((-99999999.0f > price) || (99999999.0f < price) || (0.0f > quantity) ||
            (999999.999f < quantity) || (-999.0f > discount) || (999.0f < discount))
      throw new IllegalArgumentException("The provided arguments where not valid.");

    StringBuffer data = new StringBuffer(new PrintfFormat("%-36s").sprintf(nstrcpy(name, 36)));
    data.append(";");
    data.append(taxgrp);
    data.append(";");
    data.append(getFloatFormat(price, 2));
    data.append("*");
    data.append(getFloatFormat(quantity, 3));
    if (0.0f != discount) {
      data.append(",");
      data.append(new PrintfFormat("%6.2f").sprintf(discount));
      data.append("%");
    }

    try {
      sendCommand((byte) 0x31, data.toString().getBytes("cp1251"));
    } catch (UnsupportedEncodingException e) {
      throw new PrinterErrorException(PrinterError.invalidTextEncoding());
    }
  }

  protected String getFloatFormat(float num, int count) {
    float max_value = (2 == count) ? 9999999.99f : 999999.999f;
    String match;
    if (max_value < num)
      match = "%.0f";
    else {
      match = "%010.";
      match += Integer.toString(count);
      match += "f";
    }

    String res = new PrintfFormat(match).sprintf(num).replace(',', '.');
    if ('.' == res.charAt(9))
      return res.substring(0, 9);

    return res;
  }

  protected boolean checkForZFP() throws Exception {
    return doPing(in, out, (byte) 0x04, 10);
  }

  protected boolean checkForZFPBusy() throws Exception {
    return doPing(in, out, (byte) 0x05, 10);
  }

  public void openFiscalBon(int oper, String pass, boolean detailed, boolean vat) {
    if ((9 < oper) || (1 > oper)) {
      throw new IllegalArgumentException("The provided User or pass where not valid.");
    }

    StringBuffer data = new StringBuffer(Integer.toString(oper));
    data.append(";");
    data.append(new PrintfFormat("%-4s").sprintf(nstrcpy(pass, 4)));
    data.append(detailed ? ";1" : ";0");
    data.append(vat ? ";1;0" : ";0;0");

    try {
      sendCommand((byte) 0x30, data.toString().getBytes());
    } catch (PrinterErrorException error) {
      error.addError(PrinterError.openFiscalBon());
      throw error;
    }

  }

  /**
   * Closes the opened client receipt
   */
  public void closeFiscalBon() {
    try {
      sendCommand((byte) 0x38, null);
    } catch (PrinterErrorException e) {
      e.addError(PrinterError.closeFiscalBon());
      throw e;
    }

  }

  public void printLogo() {
    sendCommand((byte) 0x6C, null);
  }

  static public String nstrcpy(String s, int maxlen) {
    if (maxlen < s.length())
      return s.substring(0, maxlen);
    return s;
  }
}
