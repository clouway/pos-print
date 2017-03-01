package com.clouway.pos.print.common;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class DateUtil {

  public static Date newDateAndTime(int year, int month, int day, int hour, int minute, int second, int millisecond){
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month-1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, millisecond);
    return calendar.getTime();
  }

  public static int hoursBetween(Date startDate, Date endDate) {

    long milis1 = startDate.getTime();
    long milis2 = endDate.getTime();

    // Calculate differences in milliseconds
    long diff = milis2 - milis1;

    // calculate differences in minutes
    // Long diffHours = diff / (60 * 1000);
    
    // calculate differences in hours
    Long diffHours = diff / (60 * 60 * 1000);

    return diffHours.intValue();
  }
}
