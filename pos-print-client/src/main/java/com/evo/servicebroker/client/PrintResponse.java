package com.evo.servicebroker.client;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class PrintResponse {
  private static final String NOT_ALLOWED_REQUEST = "Не е позволено печатане от дадената адреса.";

  private static final String SUCCESS = "Печатането е успешно.";

  private static final String PRINTING = "Чака за печат.";

  private static final String SYSTEM_ERROR = "Възникнала е състемна грешка.";

  private static final String RECEIPT_IN_QUEUE = "Бележката чака за печат.";

  private static final String RECEIPT_EXPIRED = "Времето за печатане на бележката изтече. Бележката няма да бъде отпечатана.";


  public static PrintResponse inQueue() {
    return buildResponse(false, ResponseInfo.with(RECEIPT_IN_QUEUE));
  }

  public static PrintResponse printing() {
    return buildResponse(false, ResponseInfo.with(PRINTING));
  }

  public static PrintResponse success() {
    return buildResponse(true, ResponseInfo.with(SUCCESS));
  }

  public static PrintResponse receiptExpire() {
    return buildExceptionResponse(ResponseInfo.with(RECEIPT_EXPIRED));
  }

  public static PrintResponse notAllowedRequest() {
    return buildResponse(Boolean.TRUE, ResponseInfo.with(NOT_ALLOWED_REQUEST));
  }

  public static PrintResponse systemError(Exception e) {
    return buildExceptionResponse(ResponseInfo.with(SYSTEM_ERROR), ResponseInfo.with(e.getMessage()));
  }

  public static PrintResponse withExceptionMessages(Set<ResponseInfo> error) {
    PrintResponse response = buildExceptionResponse();
    response.error = error;
    return response;
  }

  public static PrintResponse withExceptionMessages(ResponseInfo error) {
    return buildExceptionResponse(error);
  }

  private static PrintResponse buildExceptionResponse(ResponseInfo... errors){
    PrintResponse response = build(errors);
    response.success = Boolean.FALSE;
    response.isErrorResponse = Boolean.TRUE;
    return response;
  }

  private static PrintResponse buildResponse(Boolean state, ResponseInfo... errors){
    PrintResponse response = build(errors);
    response.success = state;
    response.isErrorResponse = Boolean.FALSE;
    return response;
  }

  private static PrintResponse build(ResponseInfo... errors){
    PrintResponse response = new PrintResponse();
    for(ResponseInfo message : errors){
      response.error.add(message);
    }
    return response;
  }
  private Boolean success;
  private Boolean isErrorResponse;

  private Set<ResponseInfo> error = new HashSet<ResponseInfo>();

  public Boolean isSuccess() {
    return success;
  }

  public Set<ResponseInfo> getInfo() {
    return error;
  }

  public Boolean isErrorResponse() {
    return isErrorResponse;
  }
}
