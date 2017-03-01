package com.clouway.pos.print.client.internal;

import com.clouway.pos.print.client.JsonSerializer;

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
public class NetworkCommunicatorImpl implements NetworkCommunicator {
  private final Logger log = Logger.getLogger(NetworkCommunicatorImpl.class.getName());
  private JsonSerializer serializer;

  public NetworkCommunicatorImpl(JsonSerializer serializer) {
    this.serializer = serializer;
  }

  public <T> Object sendPostRequest(String address, T t, Class<T> requestType, Class<?> responseType) {
    try {
      // Send data
      URL url = new URL(address);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setUseCaches(false);
      conn.setRequestProperty("Content-Type", "multipart/form-data");

      OutputStream out = conn.getOutputStream();

      OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
      String json = serializer.serializeEntity(t);
      writer.write(json);
      writer.flush();

      InputStream in = conn.getInputStream();

      // We need to be sure that basic type cannot be provided
      if (responseType == String.class) {
        Scanner scanner = new Scanner(in);
        StringBuffer response = new StringBuffer();
        while (scanner.hasNextLine()) {
          response.append(scanner.nextLine() + "\n");
        }
        return response.toString();
      }

      return serializer.deserializeEntity(in, responseType);
    } catch (IOException e) {
      log.log(Level.SEVERE,"Communication Exception Caught", e);
    }
    return null;
  }

  public String post(String address, String content) {
    try {
      HttpURLConnection conn = openConnection(address, "application/json");

      OutputStream out = conn.getOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
      writer.write(content);
      writer.flush();

      Scanner scanner = new Scanner(conn.getInputStream());
      StringWriter responseContent = new StringWriter();
      while (scanner.hasNext()) {
        responseContent.append(scanner.nextLine());
      }

      return responseContent.toString();
    } catch (MalformedURLException e) {
      log.log(Level.SEVERE, "Communication Exception Caught", e);

    } catch (ProtocolException e) {
      log.log(Level.SEVERE, "Communication Exception Caught", e);

    } catch (UnsupportedEncodingException e) {
      log.log(Level.SEVERE, "Communication Exception Caught", e);

    } catch (IOException e) {
      log.log(Level.SEVERE, "Communication Exception Caught", e);
    }

    return null;
  }

  @Override
  public String get(String address) {
    try {
      HttpURLConnection connection = openConnection(address, "application/json");

      BufferedReader in = new BufferedReader(
              new InputStreamReader(
                      connection.getInputStream()));
      String inputLine;

      StringBuilder builder = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        builder.append(inputLine);
      }

      in.close();

      return builder.toString();
    } catch (IOException e) {
      e.printStackTrace();
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
