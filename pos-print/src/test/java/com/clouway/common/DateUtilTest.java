package com.clouway.common;


import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class DateUtilTest {

  @Test
  public void testDates() throws InterruptedException {

    Date eidDate = DateUtil.newDate(2010, 10, 10);

    Date selectedDate = DateUtil.newDate(2010, 10, 12);
    // 12 before or equal 10
    assertFalse(DateUtil.isBeforeOrEqual(selectedDate, eidDate));
    //10 before or equal 12
    assertTrue(DateUtil.isBeforeOrEqual(eidDate, selectedDate));
    // 10 before or equal 10
    assertTrue(DateUtil.isBeforeOrEqual(eidDate, eidDate));
  }

  @Test
  public void testItShouldRecognizeDaylightSavingTime() {
    TimeZone timeZone = TimeZone.getTimeZone("EET");
    assertFalse("hm, date was in the daylight saving zone?", timeZone.inDaylightTime(DateUtil.newDate(2010, 3, 27)));
    assertTrue("hm, date was not in the daylight saving zone?", timeZone.inDaylightTime(DateUtil.newDate(2010, 3, 28)));
  }

  @Test
  public void testHoursBetweenTwoDates() {
    // 2010-06-01 9:00
    Date startDate = DateUtil.newDateAndTime(2010, 6, 1, 9, 0, 0, 0);
    // 2010-06-01 10:00
    Date endDate = DateUtil.newDateAndTime(2010, 6, 1, 10, 0, 0, 0);

    // time passed 1:00
    int hours = DateUtil.hoursBetween(startDate, endDate);
    assertNotNull(hours);
    assertEquals(1, hours);

    // 2010-06-01 9:15
    startDate = DateUtil.newDateAndTime(2010, 6, 1, 9, 15, 0, 0);
    // 2010-06-01 10:00
    endDate = DateUtil.newDateAndTime(2010, 6, 1, 10, 0, 0, 0);

    // time passed 0:45
    hours = DateUtil.hoursBetween(startDate, endDate);
    assertNotNull(hours);
    assertEquals(0, hours);

    // 2010-06-01 9:00
    startDate = DateUtil.newDateAndTime(2010, 6, 1, 9, 0, 0, 0);
    // 2010-06-01 10:15
    endDate = DateUtil.newDateAndTime(2010, 6, 1, 10, 15, 0, 0);

    // time passed 1:15
    hours = DateUtil.hoursBetween(startDate, endDate);
    assertNotNull(hours);
    assertEquals(1, hours);

    // 2010-06-01 9:00
    startDate = DateUtil.newDateAndTime(2010, 6, 1, 9, 0, 0, 0);
    // 2010-06-01 10:00
    endDate = DateUtil.newDateAndTime(2010, 6, 1, 10, 0, 59, 59);

    // time passed 1:00
    hours = DateUtil.hoursBetween(startDate, endDate);
    assertNotNull(hours);
    assertEquals(1, hours);

    // 2010-06-01 9:00
    startDate = DateUtil.newDateAndTime(2010, 6, 1, 9, 0, 0, 0);
    // 2010-06-02 10:00
    endDate = DateUtil.newDateAndTime(2010, 6, 2, 9, 0, 0, 0);

    // time passed 24:00
    hours = DateUtil.hoursBetween(startDate, endDate);
    assertNotNull(hours);
    assertEquals(24, hours);
  }

}
