package com.evo.servicebroker.client;

import java.util.Date;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class FinancialReportRequest {

  public enum Type{
    DAILY_REPORT,
    PERIOD_REPORT,
    SHORT_PERIOD_REPORT;
  }


  private String remoteIpAddress;

  private Type type;

  private Date start;

  private Date end;

  private boolean clearDailyTurnover;

  public FinancialReportRequest() {
  }

  public static FinancialReportRequest dailyReport(String remoteIpAddress, boolean clearDailyTurnover) {
    FinancialReportRequest request = new FinancialReportRequest();
    request.remoteIpAddress = remoteIpAddress;
    request.clearDailyTurnover = clearDailyTurnover;
    request.type = Type.DAILY_REPORT;
    return request;
  }

  public static FinancialReportRequest periodReport(String remoteIpAddress, Date start, Date end) {
    FinancialReportRequest request = new FinancialReportRequest();
    request.remoteIpAddress = remoteIpAddress;
    request.start = start;
    request.end = end;
    request.type = Type.PERIOD_REPORT;
    return request;
  }

  public static FinancialReportRequest shortPeriodReport(String remoteIpAddress, Date start, Date end) {
    FinancialReportRequest request = periodReport(remoteIpAddress, start, end);
    request.type = Type.SHORT_PERIOD_REPORT;
    return request;
  }

  public String getRemoteIpAddress() {
    return remoteIpAddress;
  }

  public boolean getClearDailyTurnover() {
    return clearDailyTurnover;
  }

  public Date getStart() {
    return start;
  }

  public Date getEnd() {
    return end;
  }

  public Type getType() {
    return type;
  }
}
