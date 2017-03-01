package com.clouway.pos.print.client;


/**
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class JsonSerializerFactory {

  /**
   * Creates a new JsonSerializer.
   * @return the newly created {@link JsonSerializer}
   */
  public static JsonSerializer createSerializer() {
    return new JsonSerializerFactory().build();
  }

  public JsonSerializer build() {
    return new GsonSerializerImpl();
  }
}
