package com.clouway.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class DateConverter {
  public static String convertToYearMonthDayHourSecond(Date date) {
    if (date == null) {
      return "";
    }

    return new SimpleDateFormat("yyyy.MM.dd HH:mm").format(date);
  }

  public static String convertToYearMonthDay(Date date){
    if(date == null){
      return "";
    }

    return new SimpleDateFormat("yyyy.MM.dd").format(date);
  }

  public static String convertToDayMonthYear(Date date){
    if(date == null){
      return "";
    }

    return new SimpleDateFormat("dd.MM.yyyy").format(date);
  }

  public static String convertToHourMinute(Date date) {
    if(date == null){
      return "";
    }

    return new SimpleDateFormat("HH:mm").format(date);
  }
}
