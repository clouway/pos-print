package com.clouway.servicebroker.print;

import com.clouway.servicebroker.print.internal.PrinterConnection;
import com.clouway.servicebroker.print.internal.PrinterConnector;
import com.google.inject.util.Providers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
@RunWith(JMock.class)
public class TremolPrintServiceTest {
  private Mockery context = new JUnit4Mockery();

  private final FakePrinterConnector connector = new FakePrinterConnector();

  private PrinterBase printerBase;
  private TremolPrintService service;

  @Before
  public void initialize() {
    printerBase = context.mock(PrinterBase.class);

    service = new TremolPrintService(Providers.of(printerBase),connector);
  }

  /**
   * Shows you how you can establish connection to the database and print a sample note
   *
   */
  @Test
  public void openAndCloseConnection() {
    // given closed connection
    connector.opened = false;

    context.checking(new Expectations() {{
      allowing(printerBase).openStreams();
    }});

    service.connect();

    assertThat("connection still closed?",connector.opened, is(true));

    service.disconnect();
    assertThat("connection still opened?", connector.opened,is(false));
  }

  @Test
  public void printingText() {
    final String text = "abc";
    final TextAlign align = TextAlign.RIGHT;

    context.checking(new Expectations() {{
      allowing(printerBase).printText(text, align);
    }});

    service.printText(text,align);
  }

  @Test
  public void shouldOpenFiscalBon() {
    openFiscalBon();

    service.openFiscalBon();
  }

//  @Test
//  public void closesExistingBonAndOpensNewWhenBonHasBeenAlreadyOpened() {
//
//    float lastBonAmount = 21.90f;
//
//    // open fiscal bon thrown an exception
//    pretendThatPrinterThrows(new PrinterErrorException(PrinterError.openFiscalBon()));
//
//
//    context.checking(new Expectations() {{
//
//    }});
//    // we have to retrieve existing sum,to pay it and to close the existing bone
//    expect(printerBase.calcIntermediateSum(false, false, false, 0f, '1')).andReturn(lastBonAmount);
//    payment(lastBonAmount);
//
//    printerBase.closeFiscalBon();
//
//
//    // our bon need to be opened
//    openFiscalBon();
//    replay(printerBase);
//
//    service.openFiscalBon();
//
//    verify(printerBase);
//  }

//  @Test
//  public void closesExistingBonOnOpenAndOpenNewBon() {
//
//    printerBase.openBon(1,"0");
//    expectLastCall().andThrow(new PrinterErrorException(PrinterError.openBon()));
//    printerBase.closeBon();
//    printerBase.openBon(1,"0");
//
//    replay(printerBase);
//
//    service.openBon();
//
//    verify(printerBase);
//  }

//  @Test
//  public void openBonReThrowsExceptionWhenErrorWasNotExcepted() {
//    printerBase.openBon(1,"0");
//    expectLastCall().andThrow(new PrinterErrorException(PrinterError.deviceError()));
//
//    replay(printerBase);
//    try {
//      service.openBon();
//      fail("hm, exception was not re-thrown?");
//    } catch(PrinterErrorException e) { }
//    verify(printerBase);
//  }

//  @Test
//  public void openFiscalBonReThrowsExceptionWhenErrorWasNotExcepted() {
//    printerBase.openFiscalBon(1,"0",false,false);
//    expectLastCall().andThrow(new PrinterErrorException(PrinterError.deviceError()));
//
//    replay(printerBase);
//    try {
//      service.openFiscalBon();
//      fail("hm, exception was not re-thrown?");
//    } catch(PrinterErrorException e) { }
//    verify(printerBase);
//  }

  private void pretendThatPrinterThrows(final PrinterErrorException e) {
    context.checking(new Expectations() {{
      oneOf(printerBase).openFiscalBon(1, "0", false, false);
      will(throwException(e));
    }});
  }

  private void payment(float amount) {
    printerBase.payment(amount,0,false);
  }

  private void openFiscalBon() {
    context.checking(new Expectations() {{
      allowing(printerBase).openFiscalBon(1, "0", false, false);
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
