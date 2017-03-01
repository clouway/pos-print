package com.clouway.servicebroker.service;

import com.evo.servicebroker.client.JsonSerializer;
import com.evo.servicebroker.client.JsonSerializerBuilder;
import com.google.sitebricks.client.Transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class GsonTransport implements Transport {

  private JsonSerializer serializer = JsonSerializerBuilder.createSerializer();

  public <T> T in(InputStream inputStream, Class<T> tClass) throws IOException {
    return (T) serializer.deserializeEntity(inputStream, tClass);
  }

  public <T> void out(OutputStream outputStream, Class<T> tClass, T t) {
    serializer.serializeEntity(outputStream,tClass,t);
  }

  public String contentType() {
    return "text/json";
  }
}
