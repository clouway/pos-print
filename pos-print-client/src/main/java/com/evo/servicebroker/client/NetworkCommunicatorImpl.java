package com.evo.servicebroker.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class NetworkCommunicatorImpl implements NetworkCommunicator {
  private final Logger log = Logger.getLogger(NetworkCommunicatorImpl.class.getName());
  private JsonSerializer serializer;

  public NetworkCommunicatorImpl(JsonSerializer serializer) {
    this.serializer = serializer;
  }

  public <T> Object sendPostRequest(String address, T t, Class<T> clazz) {
    try {
      // Send data
      URL url = new URL(address);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setUseCaches(false);
      conn.setReadTimeout(10000);
      conn.setRequestProperty("Content-Type", "multipart/form-data");

      OutputStream out = conn.getOutputStream();

      OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
      serializer.serializeEntity(writer, clazz, t);
      writer.flush();

      InputStream in = conn.getInputStream();

      InputStreamReader reader = new InputStreamReader(in, "UTF-8");
      Scanner scanner = new Scanner(reader);
      StringBuffer content = new StringBuffer();
      while (scanner.hasNextLine()) {
        content.append(scanner.nextLine());
      }
      log.info("Json: " + content.toString());

      return serializer.deserializeEntity(new ByteArrayInputStream(content.toString().getBytes()), PrintResponse.class);
    } catch (IOException e) {
      log.log(Level.SEVERE,"Communication Exception Caught", e);
      throw ConnectionException.invalidResponse();
    }
  }

  public String post(String address, String content, boolean async) {
    try {
      HttpURLConnection conn = openConnection(address, "application/json");

      if (async) {
        conn.addRequestProperty("async","async");
      }

      OutputStream out = conn.getOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
      writer.write(content);
      writer.flush();

      Scanner scanner = new Scanner(conn.getInputStream());
      StringWriter responseContent = new StringWriter();
      while (scanner.hasNext()) {
        responseContent.append(scanner.nextLine());
      }
    } catch (MalformedURLException e) {
      log.log(Level.SEVERE, "Communication Exception Caught", e);
      throw ConnectionException.invalidResponse();
    } catch (ProtocolException e) {
      log.log(Level.SEVERE, "Communication Exception Caught", e);
      throw ConnectionException.invalidResponse();
    } catch (UnsupportedEncodingException e) {
      log.log(Level.SEVERE, "Communication Exception Caught", e);
      throw ConnectionException.invalidResponse();
    } catch (IOException e) {
      log.log(Level.SEVERE, "Communication Exception Caught", e);
      throw ConnectionException.invalidResponse();
    }

    return null;
  }

  private HttpURLConnection openConnection(String address, String contentType) throws IOException {
    URL url = new URL(address);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setUseCaches(false);
    conn.setRequestProperty("Content-Type", contentType);
    return conn;
  }
}
