package com.evo.servicebroker.client;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
@RunWith(JMock.class)
public class RealServiceBrokerTest {
  private Mockery context = new JUnit4Mockery();

  private RealServiceBrokerClient client;

  private NetworkCommunicator communicator;
  private Sleeper sleep ;
  private String url = "urlAddress";
  private String printReceiptUrl = url + "/printReceipt";

  @Before
  public void before() {
    communicator = context.mock(NetworkCommunicator.class);
    sleep = context.mock(Sleeper.class);

    client = new RealServiceBrokerClient(url, communicator, sleep);
  }

  @Test
  public void responseFromPrintingOfReceiptIsReturned() {
    final String printContent = "Some content to be send to server for printing";
    final PrintResponse success = PrintResponse.success();

    context.checking(new Expectations() {{
      oneOf(communicator).sendPostRequest(printReceiptUrl, printContent, String.class);
      will(returnValue(success));
    }});

    PrintResponse clientResponse = client.print(printContent, String.class);

    assertThat(clientResponse, is(sameInstance(success)));
  }

  @Test
  public void inQueueResponsesAreRetriedMultipleTimes() {
    final String content = "content";

    context.checking(new Expectations() {{

      exactly(3).of(communicator).sendPostRequest(printReceiptUrl, content, String.class);
      will(returnValue(PrintResponse.inQueue()));

      oneOf(communicator).sendPostRequest(printReceiptUrl, content, String.class);
      will(returnValue(PrintResponse.success()));

      exactly(3).of(sleep).sleep(with(any(Integer.class)));
    }});

    PrintResponse response = client.print(content, String.class);

    assertTrue(response.isSuccess());
    assertFalse(response.isErrorResponse());
    assertEquals(1, response.getInfo().size());
  }

  @Test
  public void testPrintReceiptReturnErrorResponse() {
    final String content = "content";

    final Set<ResponseInfo> errorMessages = new HashSet<ResponseInfo>();
    errorMessages.add(ResponseInfo.with("No paper"));

    context.checking(new Expectations() {{
      oneOf(communicator).sendPostRequest(printReceiptUrl, content, String.class);
      will(returnValue(PrintResponse.withExceptionMessages(errorMessages)));
    }});


    PrintResponse response = client.print(content, String.class);

    assertTrue(response.isErrorResponse());
    assertEquals(errorMessages, response.getInfo());
  }

  @Test(expected = ConnectionException.class)
  public void communicationExceptionsAreNotHandledInternally() {
    final String content = "content";

    context.checking(new Expectations() {{
      oneOf(communicator).sendPostRequest(printReceiptUrl, content, String.class);
      will(throwException(new ConnectionException("asd")));
    }});

    client.print(content, String.class);

  }

}
