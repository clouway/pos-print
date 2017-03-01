package com.clouway.pos.print.client;

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

  <T> T deserializeEntity(InputStream in, Type type);

  <T> String serializeEntity(T entity);

}
