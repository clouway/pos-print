package com.clouway.pos.print.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;


/**
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class GsonSerializerImpl implements JsonSerializer {

  public <T> String serializeEntity(T entity) {
    return getGson().toJson(entity);
  }

  public Object deserializeEntity(InputStream in, Type type) {
    try {
      return getGson().fromJson(new InputStreamReader(in,"UTF-8"), type);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  protected Gson getGson() {
    GsonBuilder builder = new GsonBuilder();
    builder.serializeNulls();
    return builder.create();
  }
}
