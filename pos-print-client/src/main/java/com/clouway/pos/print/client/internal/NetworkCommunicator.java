package com.clouway.pos.print.client.internal;

/**
 * 
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface NetworkCommunicator {

  <T> Object sendPostRequest(String address, T t, Class<T> requestType, Class<?> responseType);

  String post(String address, String content);

  String get(String address);

}
