package com.clouway.pos.print.service;

import com.clouway.pos.print.client.JsonSerializer;
import com.clouway.pos.print.client.JsonSerializerFactory;
import com.google.inject.TypeLiteral;
import com.google.sitebricks.client.Transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public final class GsonTransport implements Transport {

  private JsonSerializer serializer = JsonSerializerFactory.createSerializer();

  public <T> T in(InputStream inputStream, Class<T> tClass) throws IOException {
    return (T) serializer.deserializeEntity(inputStream, tClass);
  }

  @Override
  public <T> T in(InputStream in, TypeLiteral<T> type) throws IOException {
    return null;
  }

  public <T> void out(OutputStream outputStream, Class<T> tClass, T t) {
    String json = serializer.serializeEntity(t);
    try {
      outputStream.write(json.getBytes("UTF8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String contentType() {
    return "application/json";
  }
}
