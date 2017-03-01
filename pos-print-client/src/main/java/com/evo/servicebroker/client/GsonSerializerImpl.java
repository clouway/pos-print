package com.evo.servicebroker.client;

import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Miroslav Genov (mgenov@gmail.com)
 */
class GsonSerializerImpl implements JsonSerializer {


  private class JdoExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipField(FieldAttributes fieldAttributes) {

      for (String field : exclusionFields) {
        if (fieldAttributes.getName().contains(field)) {
          return true;
        }
      }

      return false;
    }

    public boolean shouldSkipClass(Class<?> aClass) {

      for (Class exclusion : exclusionClasses) {
        if (exclusion == aClass) {
          return true;
        }
      }
      
      return false;
    }
  }

  private final String[] exclusionFields;
  private final Class[] exclusionClasses;
  private Map<Class, TypeAdapter> typeAdapters = new HashMap<Class, TypeAdapter>();

  public GsonSerializerImpl(String[] exclusionFields, Class[] exclusionClasses, Map<Class, TypeAdapter> typeAdapters) {
    this.exclusionFields = exclusionFields;
    this.exclusionClasses = exclusionClasses;
    this.typeAdapters = typeAdapters;
  }

  public <T> String serializeEntity(T entity) {
    return getGson().toJson(entity);
  }
  
  public <T> void serializeEntity(T entity, Appendable writer) {
     getGson().toJson(entity,writer);
  }

  public <T> String serializeEntities(Collection<T> entities) {
    return getGson().toJson(entities);
  }

  public <T> void serializeEntity(OutputStream out, Class<T> type, T data) {
    getGson().toJson(data, type, new PrintStream(out));
  }

  public <T> void serializeEntity(OutputStreamWriter out, Class<T> type, T data) {
    getGson().toJson(data, type, out);
  }

  public Object deserializeEntities(String json,Type type) {
    Object item = getGson().fromJson(json, type);
    return item;
  }

  public Object deserializeEntity(InputStream in, Type type) {
    try {
      return getGson().fromJson(new InputStreamReader(in,"UTF8"), type);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Object deserializeEntity(Reader in, Type type) {
    return getGson().fromJson(in, type);
  }

  protected Gson getGson() {
    GsonBuilder builder = new GsonBuilder();
    builder.setExclusionStrategies(new JdoExclusionStrategy());
    builder.serializeNulls();

    for(Class clazz : typeAdapters.keySet()){
      TypeAdapter typeAdapter = typeAdapters.get(clazz);
      builder.registerTypeAdapter(clazz, typeAdapter.getSerializer());
      builder.registerTypeAdapter(clazz, typeAdapter.getDeserializer());
    }

    return builder.create();
  }
}
