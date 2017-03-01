package com.evo.servicebroker.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * JsonSerializerBuilder is the entry point of json serialization classes. The current version of that class is using <code>gson</code> library
 * but in the future version this could be changed.
 * <p/>
 * <p>
 * To create new JsonSerializer who does not exclude any class or field and using the default date format <code>yyyy-MM-dd HH:mm:ss</code> use:
 * <pre>
 *    new JsonSerializerBuilder().build();
 *  </pre>
 * who return newly created {@link com.evo.servicebroker.client.JsonSerializer}
 * </p>
 *
 * <p>
 * To create new JsonSerializer with exclusion fields and classes, and using the default date format <code>yyyy-MM-dd HH:mm:ss</code> use:
 * <pre>
 *  new JsonSerializerBuilder().excludeFields("fieldOne", "fieldTwo", ...).excludeClasses(String.class, Date.class, ...).build();
 *  </pre>
 * who return newly created {@link com.evo.servicebroker.client.JsonSerializer};
 * </p>
 *
 * <p>
 * To create new JsonSerializer by providing the data pettern, the array of fields that need to be ignored by the serializer
 * and a list of exclusion classes that need to be ignored too, use:
 * <pre>
 *    JsonSerializerBuilder.datePattern("yyyy-MM-dd HH:mm:ss").excludeFields("fieldOne", "fieldTwo", ...).excludeClasses(String.class, Date.class, ...).build();
 *  </pre>
 *  If no datePattern was specified then the builder is using the default date format <code>yyyy-MM-dd HH:mm:ss</code>
 * </p>
 *
 * <p>
 *   To create new JsonSerializer with type adapter use:
 *   <pre>
 *     new JsonSerializerBuilder()
 *     .typeAdapter(User.class, new com.google.gson.JsonSerializer<User>(), new com.google.gson.JsonDeserializer<User>()).build();
 *   </pre>
 *
 *   Type adapter for converting Date in Bulgarian locale is used by default. To define your own adapter use:
 *   <pre>
 *     new JsonSerializerBuilder()
 *     .typeAdapter(Date.class, new com.google.gson.JsonSerializer<Date>(), new com.google.gson.JsonDeserializer<Date>()).build();
 *   </pre>
 *   This will override default date adapter.
 *
 *   Only one adapter (Serializer and Deserializer) can be used by type. There cant be provided two adapters for same type.
 * </p>
 *
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class JsonSerializerBuilder {

  /**
   * Creates a new JsonSerializer. The created serializer is not excluding any class or field and is using the default date format
   * <code>yyyy-MM-dd HH:mm:ss</code>.
   *
   * @return the newly created {@link com.evo.servicebroker.client.JsonSerializer}
   */
  public static JsonSerializer createSerializer() {
    return new JsonSerializerBuilder().build();
  }

  //TODO: Date formats could be replaced by constant values that could be changed by the development environment
  private String datePattern = "yyyy-MM-dd HH:mm:ss";

  // TODO: Locale could be replaced by constant values that could be changed by the development environment
  private Locale locale = new Locale("bg_BG");

  private String[] excludeFields = new String[]{};
  private Class[] excludeClass = new Class[]{};

  private Map<Class, TypeAdapter> typeAdapters = new HashMap<Class, TypeAdapter>();

  public JsonSerializerBuilder datePattern(String datePattern) {
    this.datePattern = datePattern;
    return this;
  }

  public JsonSerializerBuilder excludeFields(String... excludeFields) {
    this.excludeFields = excludeFields;
    return this;
  }

  public JsonSerializerBuilder excludeClasses(Class... excludeClass) {
    this.excludeClass = excludeClass;
    return this;
  }

  public <T> JsonSerializerBuilder typeAdapter(Class<T> clazz, com.google.gson.JsonSerializer<T> serialized, JsonDeserializer<T> deserializer) {
    typeAdapters.put(clazz, new TypeAdapter(serialized, deserializer));
    return this;
  }

  public JsonSerializer build() {
    // todo: make it configurable
    typeAdapters.put(Date.class, new TypeAdapter(new CustomDateSerializer(datePattern), new CustomDateParser(datePattern)));

    return new GsonSerializerImpl(excludeFields, excludeClass, typeAdapters);
  }
  private class CustomDateSerializer implements com.google.gson.JsonSerializer<Date> {

    private String datePattern;

    private CustomDateSerializer(String datePattern) {
      this.datePattern = datePattern;
    }
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
      final SimpleDateFormat sdf = new SimpleDateFormat(datePattern, locale);

      Calendar today = Calendar.getInstance();
      today.setTime(src);

      return new JsonPrimitive(sdf.format(today.getTime()));
    }

  }

  private class CustomDateParser implements JsonDeserializer<Date> {

    private String datePattern;

    private CustomDateParser(String datePattern) {
      this.datePattern = datePattern;
    }

    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      final SimpleDateFormat sdf = new SimpleDateFormat(datePattern, locale);

      try {
        return sdf.parse(jsonElement.getAsString());
      } catch (ParseException e) {
        throw new JsonParseException("Date value of " + jsonElement.getAsString() + " was not valid value.");
      }
    }

  }
}
