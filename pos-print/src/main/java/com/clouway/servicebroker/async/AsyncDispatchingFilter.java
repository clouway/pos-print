package com.clouway.servicebroker.async;

import com.google.common.io.ByteStreams;
import com.google.inject.Singleton;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.jetty.client.ContentExchange;
import org.mortbay.jetty.client.HttpClient;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

/**
 * AsyncDispatchingFilter is a Servlet Filter  that is providing the system with ability to schedule synchronous requests to be executed as asynchronous. To that the
 * Filter is checking whether the request contains header parameter named <code>async</code>. The following parameter is used as a marker which requests
 * to be executed directly and which of them to be scheduled for async execution.
 * <p/>
 * The general purpose of the Async request handling is to allow on heavy requests to be handled in the background, so the client application doesn't have to wait untill
 * application finishes it's execution.
 * <p/>
 * <p/>
 * Here is a small example, that is using java's URL to send handle the provided request in async manner.
 * <pre>
 *   URLConnection cnx = url.openConnection();
 *   cnx.setAllowUserInteraction(false);
 *   cnx.setDoInput(true);
 *   cnx.setDoOutput(true);
 *   cnx.addRequestProperty("async","async");
 * </pre>
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
@Singleton
public class AsyncDispatchingFilter implements Filter {
  private HttpClient client;

  public void init(FilterConfig filterConfig) throws ServletException {
    client = new HttpClient();
    client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
    try {
      client.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;

    String async = request.getHeader("async");

    if ("async".equals(async)) {
      ContentExchange exchange = new ContentExchange() {

        // define the callback method to process the response when you get it back
        protected void onResponseComplete() throws IOException {
          super.onResponseComplete();
        }

      };
      String targetAddress = request.getRequestURL().toString();
      exchange.setMethod(request.getMethod());
      exchange.setURL(targetAddress);

      makeRequestParametersAvailableInTheAsyncRequest(request, exchange);
      makeHeadersAvailableInTheAsyncRequest(request, exchange);

      client.send(exchange);

      PrintWriter writer = servletResponse.getWriter();
      writer.println("OK");
      writer.flush();
      writer.close();
    } else {

      // We are processing our request synchronously
      filterChain.doFilter(request, servletResponse);
    }
  }

  private void makeRequestParametersAvailableInTheAsyncRequest(HttpServletRequest request, ContentExchange exchange) throws IOException {
    byte[] parametersContent = ByteStreams.toByteArray(request.getInputStream());
    StringBuffer requestParameters = new StringBuffer(new String(parametersContent));

    /**
     * We need to be sure that all request parameters will be available and in the async request
     */
    Map<String, String[]> requestMap = request.getParameterMap();
    for (String key : requestMap.keySet()) {
      String parameter = request.getParameter(key);

      if (requestParameters.length() != 0) {
        requestParameters.append("&");
      }

      requestParameters.append(key + "=" + parameter);
    }
    exchange.setRequestContent(new ByteArrayBuffer(requestParameters.toString()));
    exchange.setRequestContentType(request.getContentType());
  }

  private void makeHeadersAvailableInTheAsyncRequest(HttpServletRequest request, ContentExchange exchange) {
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerKey = headerNames.nextElement();
      String headerValue = request.getHeader(headerKey);

      /**
       *  The specific async header need to be skipped, cause we don't want to fall in an endless loop.
       */
      if (!"async".equals(headerKey)) {
        exchange.addRequestHeader(headerKey, headerValue);
      }
    }
  }

  public void destroy() {

  }
}
