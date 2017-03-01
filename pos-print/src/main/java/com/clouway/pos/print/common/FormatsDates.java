package com.clouway.pos.print.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class FormatsDates {
  public static String toYearMonthDayHourSecond(Date date) {
    if (date == null) {
      return "";
    }

    return new SimpleDateFormat("yyyy.MM.dd HH:mm").format(date);
  }

  public static String toYearMonthDay(Date date) {
    if (date == null) {
      return "";
    }

    return new SimpleDateFormat("yyyy.MM.dd").format(date);
  }
}
