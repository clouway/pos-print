package com.evo.servicebroker.client;

import java.util.Date;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class ResponseInfo {
  public static ResponseInfo with(String infoMessage) {
    ResponseInfo info = new ResponseInfo();
    info.infoMessage = infoMessage;
    info.trackNumber = new Date().getTime();
    return info;
  }

  private String infoMessage;
  private long trackNumber;
  private ResponseInfo(){}

  public String getInfoMessage(){
    return infoMessage;
  }

  public long getTrackNumber() {
    return trackNumber;
  }
}
