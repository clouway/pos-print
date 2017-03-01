package com.clouway.servicebroker.service;

import com.clouway.servicebroker.ConcurrentReceiptContainer;
import com.clouway.servicebroker.QueuePrinterMonitor;
import com.clouway.servicebroker.PrinterMonitor;
import com.clouway.servicebroker.ReceiptContainer;
import com.clouway.servicebroker.ReceiptPrintService;
import com.evo.servicebroker.client.JsonSerializer;
import com.evo.servicebroker.client.JsonSerializerBuilder;
import com.evo.servicebroker.client.Receipt;
import com.clouway.servicebroker.print.Printer;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class PrintReceiptServiceTest {

  @Inject
  private PrintReceiptService printReceiptService;

  @Inject
  private ReceiptContainer container;

  @Inject
  private JsonSerializer serializer;

  private MockReceiptPrintService mockPrintService;
  private HttpServletRequest httpRequest;
  private Map<String, Receipt> receipts = new HashMap<String, Receipt>();
  private Map<String, String> httpReceiptKeys = new HashMap<String, String>();
  private MockPrintingThread printingOne;
  private MockPrintingThread printingTwo;

  @Before
  public void before() {
    mockPrintService = new MockReceiptPrintService();
    httpRequest = new MockHttpRequest();
    printingOne = new MockPrintingThread();
    printingTwo = new MockPrintingThread();

    Module module = new AbstractModule() {

      @Override
      protected void configure() {
        bind(ReceiptPrintService.class).toInstance(mockPrintService);
        bind(ReceiptContainer.class).to(ConcurrentReceiptContainer.class);
        bind(PrinterMonitor.class).to(QueuePrinterMonitor.class);
        bind(JsonSerializer.class).toInstance(JsonSerializerBuilder.createSerializer());
      }

      @Provides
      public HttpServletRequest getHttp() {
        return httpRequest;
      }

      @Provides
      public Receipt getReceipt() {
        String name = Thread.currentThread().getName();
        return receipts.get(name);
      }

      @Provides
      public Printer getPrinter() {
        return Printer.with("printerName");
      }
    };
    Guice.createInjector(module).injectMembers(this);
  }

  @Test
  public void testPrintDifferentReceiptFromDifferentThreadsOnSamePrinter() throws InterruptedException {
    prepareMock();
    printingOne.start();
    printingTwo.start();

    Thread.sleep(1500);

    assertEquals(1, mockPrintService.counter);

    Thread.sleep(3000);

    assertEquals(2, mockPrintService.counter);
  }

  private void prepareMock() {
    receipts.put(printingOne.getName(), Receipt.with("receiptKey", "receiptIp", null, "", "", null, null, null, null,""));
    receipts.put(printingTwo.getName(), Receipt.with("receiptKey2", "receiptIp", null, "", "", null, null, null, null,""));

    httpReceiptKeys.put(printingOne.getName(), "receiptKey");
    httpReceiptKeys.put(printingTwo.getName(), "receiptKey2");

    for(Receipt receipt : receipts.values()){
      container.addReceipt(receipt);
    }
  }

  class MockPrintingThread extends Thread {

    @Override
    public void run() {
      printReceiptService.proceed();
    }

  }

  class MockReceiptPrintService implements ReceiptPrintService {
    public int counter = 0;
    public Exception e = null;

    public void printReceipt() throws InterruptedException {
      if(e!=null){
        throw new RuntimeException(e);
      }

      counter++;
      Thread.sleep(2000);
    }

  }

  class MockHttpRequest implements HttpServletRequest {

    public String getAuthType() {
      return null;
    }

    public Cookie[] getCookies() {
      return new Cookie[0];
    }

    public long getDateHeader(String s) {
      return 0;
    }

    public String getHeader(String s) {
      return null;
    }

    public Enumeration getHeaders(String s) {
      return null;
    }

    public Enumeration getHeaderNames() {
      return null;
    }

    public int getIntHeader(String s) {
      return 0;
    }

    public String getMethod() {
      return null;
    }

    public String getPathInfo() {
      return null;
    }

    public String getPathTranslated() {
      return null;
    }

    public String getContextPath() {
      return null;
    }

    public String getQueryString() {
      return null;
    }

    public String getRemoteUser() {
      return null;
    }

    public boolean isUserInRole(String s) {
      return false;
    }

    public Principal getUserPrincipal() {
      return null;
    }

    public String getRequestedSessionId() {
      return null;
    }

    public String getRequestURI() {
      return null;
    }

    public StringBuffer getRequestURL() {
      return null;
    }

    public String getServletPath() {
      return null;
    }

    public HttpSession getSession(boolean b) {
      return null;
    }

    public HttpSession getSession() {
      return null;
    }

    public boolean isRequestedSessionIdValid() {
      return false;
    }

    public boolean isRequestedSessionIdFromCookie() {
      return false;
    }

    public boolean isRequestedSessionIdFromURL() {
      return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
      return false;
    }

    public Object getAttribute(String s) {
      return null;
    }

    public Enumeration getAttributeNames() {
      return null;
    }

    public String getCharacterEncoding() {
      return null;
    }

    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    public int getContentLength() {
      return 0;
    }

    public String getContentType() {
      return null;
    }

    public ServletInputStream getInputStream() throws IOException {
      return null;
    }

    public String getParameter(String s) {
      String name = Thread.currentThread().getName();
      return httpReceiptKeys.get(name);
    }

    public Enumeration getParameterNames() {
      return null;
    }

    public String[] getParameterValues(String s) {
      return new String[0];
    }

    public Map getParameterMap() {
      return null;
    }

    public String getProtocol() {
      return null;
    }

    public String getScheme() {
      return null;
    }

    public String getServerName() {
      return null;
    }

    public int getServerPort() {
      return 0;
    }

    public BufferedReader getReader() throws IOException {
      return null;
    }

    public String getRemoteAddr() {
      return null;
    }

    public String getRemoteHost() {
      return null;
    }

    public void setAttribute(String s, Object o) {

    }

    public void removeAttribute(String s) {

    }

    public Locale getLocale() {
      return null;
    }

    public Enumeration getLocales() {
      return null;
    }

    public boolean isSecure() {
      return false;
    }

    public RequestDispatcher getRequestDispatcher(String s) {
      return null;
    }

    public String getRealPath(String s) {
      return null;
    }

    public int getRemotePort() {
      return 0;
    }

    public String getLocalName() {
      return null;
    }

    public String getLocalAddr() {
      return null;
    }

    public int getLocalPort() {
      return 0;
    }
  }


}
