package com.evo.servicebroker.client;

/**
 * 
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
interface NetworkCommunicator {

  <T> Object sendPostRequest(String address, T t, Class<T> clazz);

  String post(String address, String content,boolean async);

}
