package com.evo.servicebroker.client;

import com.google.gson.*;
import com.google.gson.JsonSerializer;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class TypeAdapter {
  private com.google.gson.JsonSerializer serializer;
  private JsonDeserializer deserializer;

  <T> TypeAdapter(JsonSerializer<T> serializer, JsonDeserializer<T> deserializer) {
    this.serializer = serializer;
    this.deserializer = deserializer;
  }

  public JsonSerializer getSerializer() {
    return serializer;
  }

  public JsonDeserializer getDeserializer() {
    return deserializer;
  }
}
