package com.clouway.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class DateUtil {

  private static final Logger log = Logger.getLogger(DateUtil.class.getName());

  private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * @param d1
   * @param d2
   * @return
   */
  public static boolean isBeforeOrEqual(Date d1, Date d2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();

    TimeZone timeZone = TimeZone.getTimeZone("EET");
    String timeZoneValue = "GMT+2";

    // we are in daily saving time which means that one hour more need to be added.
    if (timeZone.inDaylightTime(d1)) {
     log.info("Daylight saving time");
     timeZoneValue = "GMT+300";
    }

    log.info("Using timezone: " + timeZoneValue);

    d1 = getDateInTimeZone(d1,timeZoneValue);
    d2 = getDateInTimeZone(d2,timeZoneValue);


    cal1.setTime(d1);
    cal2.setTime(d2);

    int day1 = cal1.get(Calendar.DAY_OF_MONTH);
    int month1 = cal1.get(Calendar.MONTH);
    int year1 = cal1.get(Calendar.YEAR);

    int day2 = cal2.get(Calendar.DAY_OF_MONTH);
    int month2 = cal2.get(Calendar.MONTH);
    int year2 = cal2.get(Calendar.YEAR);

    if (day1 == day2 && month1 == month2 && year1 == year2) {
      return true;
    }

    cal1 = Calendar.getInstance();
    cal2 = Calendar.getInstance();

    cal1.set(Calendar.YEAR, year1);
    cal1.set(Calendar.MONTH, month1);
    cal1.set(Calendar.DAY_OF_MONTH, day1);

    log.info("date 1 : " + year1 + "-" + month1 + "-" + day1);

    cal2.set(Calendar.YEAR, year2);
    cal2.set(Calendar.MONTH, month2);
    cal2.set(Calendar.DAY_OF_MONTH, day2);

    log.info("date 2 : " + year2 + "-" + month2 + "-" + day2);

    log.info("date1 = " + cal1.getTime());
    log.info("date2 = " + cal2.getTime());

    clearNonDateFields(cal1);
    clearNonDateFields(cal2);

    if (cal1.before(cal2) || cal1.equals(cal2)) {
      return true;
    }

    return false;
  }

  public static Date newDate(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);

    clearNonDateFields(calendar);
    return calendar.getTime();
  }

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

  private static Calendar clearNonDateFields(Calendar c) {
    c.clear(Calendar.HOUR);
    c.clear(Calendar.MINUTE);
    c.clear(Calendar.SECOND);
    c.clear(Calendar.MILLISECOND);
    return c;
  }


  private static Date getDateInTimeZone(Date currentDate, String timeZoneId){

    TimeZone tz = TimeZone.getTimeZone(timeZoneId);

    Calendar mbCal = Calendar.getInstance(tz);

    mbCal.setTimeInMillis(currentDate.getTime());

    Calendar cal = Calendar.getInstance();

    cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));

    cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));

    cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));

    cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));

    cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));

    cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));

    cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));

    return cal.getTime();

  }

  /**
   * Returns the current date. Please note that the function is cleaning time information.
   *
   * @return the current date without time information
   */
  public static Date now() {
    Date now = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(now);

    clearNonDateFields(calendar);
    return calendar.getTime();
  }

  public static int hoursBetween(Date startDate, Date endDate) {
    Calendar start = Calendar.getInstance();
    Calendar end = Calendar.getInstance();

    start.setTime(startDate);
    end.setTime(endDate);

    long milis1 = start.getTimeInMillis();
    long milis2 = end.getTimeInMillis();

    // Calculate differences in milliseconds
    long diff = milis2 - milis1;

    // calculate differences in minutes
    // Long diffHours = diff / (60 * 1000);
    
    // calculate differences in hours
    Long diffHours = diff / (60 * 60 * 1000);

    return diffHours.intValue();
  }
}
