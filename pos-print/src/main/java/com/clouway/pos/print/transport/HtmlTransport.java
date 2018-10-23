package com.clouway.pos.print.transport;

import com.google.inject.TypeLiteral;
import com.google.sitebricks.client.Transport;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class HtmlTransport implements Transport {

  @Override
  public <T> T in(InputStream inputStream, Class<T> tClass) throws IOException {
    String content = IOUtils.toString(inputStream);
    return (T) content;
  }

  @Override
  public <T> T in(InputStream inputStream, TypeLiteral<T> typeLiteral) throws IOException {
    String content = IOUtils.toString(inputStream);
    return (T) content;
  }

  @Override
  public <T> void out(OutputStream outputStream, Class<T> tClass, T t) throws IOException {
    String content = (String) t;
    IOUtils.write(content, outputStream, "UTF-8");
  }

  @Override
  public String contentType() {
    return "text/html";
  }
}