package com.evo.servicebroker.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface JsonSerializer {

  <T> void serializeEntity(T entity, Appendable writer);

  <T> String serializeEntities(Collection<T> entities);

  Object deserializeEntities(String json,Type type);

  Object deserializeEntity(InputStream in, Type type);

  Object deserializeEntity(Reader in, Type type);

  <T> void serializeEntity(OutputStream out, Class<T> type, T data);

  <T> void serializeEntity(OutputStreamWriter out, Class<T> type, T data);

  <T> String serializeEntity(T entity);

}
