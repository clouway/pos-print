package com.clouway.pos.print.printer.tremol;

import com.clouway.pos.print.printer.PrinterConnection;
import com.clouway.pos.print.printer.PrinterConnector;
import com.clouway.pos.print.printer.PrinterError;
import com.clouway.pos.print.printer.PrinterErrorException;
import com.clouway.pos.print.printer.TextAlign;
import com.clouway.pos.print.printer.tremol.PrinterBase;
import com.google.inject.util.Providers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
@RunWith(JMock.class)
public class TremolPrintServiceTest {

  private final FakePrinterConnector connector = new FakePrinterConnector();

  private Mockery context = new JUnit4Mockery();

  private PrinterBase printerBase = context.mock(PrinterBase.class);

  private final TremolPrintService service = new TremolPrintService(Providers.of(printerBase), connector);


  /**
   * Shows you how you can establish connection to the database and print a sample note
   */
  @Test
  public void openAndCloseConnection() {
    // given closed connection
    connector.opened = false;


    context.checking(new Expectations() {{
      oneOf(printerBase).openStreams();
    }});

    service.connect();

    assertThat("connection still closed?", connector.opened, is(true));

    service.disconnect();
    assertThat("connection still opened?", connector.opened, is(false));
  }

  @Test
  public void printingText() {

    final String text = "abc";
    final TextAlign align = TextAlign.RIGHT;

    context.checking(new Expectations() {{
      oneOf(printerBase).printText(text, align);
    }});

    service.printText(text, align);
  }

  @Test
  public void shouldOpenFiscalBon() {

    openFiscalBon();

    service.openFiscalBon();
  }

  @Test
  public void closesExistingBonAndOpensNewWhenBonHasBeenAlreadyOpened() {

    final float lastBonAmount = 21.90f;

    pretendOpenFiscalBonFailsWith(new PrinterErrorException(PrinterError.openFiscalBon()));

    context.checking(new Expectations() {{
      oneOf(printerBase).closeBon();

      oneOf(printerBase).calcIntermediateSum(false, false, false, 0f, '1');
      will(returnValue(lastBonAmount));

      oneOf(printerBase).payment(lastBonAmount, 0, false);

      allowing(printerBase).closeFiscalBon();
      allowing(printerBase).openFiscalBon(1, "0", false, false);
    }});

    service.openFiscalBon();
  }

  @Test
  public void closesExistingBonOnOpenAndOpenNewBon() {

    context.checking(new Expectations() {{
      oneOf(printerBase).openBon(1, "0");
      will(throwException(new PrinterErrorException(PrinterError.openBon())));

      oneOf(printerBase).closeFiscalBon();
      oneOf(printerBase).closeBon();
      oneOf(printerBase).openBon(1, "0");
    }});

    service.openBon();
  }

  @Test
  public void openBonReThrowsExceptionWhenErrorWasNotExcepted() {
    context.checking(new Expectations() {{
      oneOf(printerBase).openBon(1, "0");
      will(throwException(new PrinterErrorException(PrinterError.deviceError())));
    }});
    try {
      service.openBon();
      fail("hm, exception was not re-thrown?");
    } catch (PrinterErrorException e) {
    }
  }

  @Test
  public void openFiscalBonReThrowsExceptionWhenErrorWasNotExcepted() {

    context.checking(new Expectations() {{
      oneOf(printerBase).openFiscalBon(1, "0", false, false);
      will(throwException(new PrinterErrorException(PrinterError.deviceError())));
    }});

    try {
      service.openFiscalBon();
      fail("hm, exception was not re-thrown?");
    } catch (PrinterErrorException e) {
    }
  }

  private void payment(float amount) {
    printerBase.payment(amount, 0, false);
  }

  private void openFiscalBon() {
    context.checking(new Expectations() {{
      oneOf(printerBase).openFiscalBon(1, "0", false, false);
    }});
  }

  private void pretendOpenFiscalBonFailsWith(final Exception e) {
    context.checking(new Expectations() {{
      oneOf(printerBase).openFiscalBon(1, "0", false, false);
      will(throwException(e));
    }});
  }


  class FakePrinterConnector implements PrinterConnector {
    public boolean opened = false;

    public void openConnection() {
      opened = true;
    }

    public void closeConnection() {
      opened = false;
    }

    public PrinterConnection getCurrentConnection() {
      return null;
    }
  }

}
