package com.clouway.servicebroker.print;

import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TimeLogger {
  private final Logger log;
  private long start = -1l;

  /**
   * Get an instance for timer.
   * @param clazz class where this timer is used.
   * @return new instance of TimerLog.
   */
  public static TimeLogger getTimeLog(Class clazz){
    return new TimeLogger(clazz);
  }

  private TimeLogger(Class clazz) {
    log = Logger.getLogger(clazz.getName());
  }

  /**
   * Start timer. Moment when this method is called counting begin.
   * If you call this method 2 times, timer start time will be from second method call.
   */
  public void start(){
    start = new Date().getTime();
  }

  /**
   * Stop timer and log passed time. This Method log passed time between starting the timer and calling this method.
   * Log message will look like this "key executed in: xx ms" where key is a given string in this method and xx is passed time.
   * If start method is not called before end method, logger will log 0 ms.
   *
   * @param key given string to be placed as first word in log message.
   */
  public void stop(String key){
    long passedTime = 0l;
    if(start >= 0){
      Long end = new Date().getTime();
      passedTime = end - start;
      start = -1l;
    }

    log.info(key + " executed in: " + passedTime + " ms");

  }

  public void resetAndLog(String key){
    long passedTime = 0l;
    if(start >= 0){
      Long end = new Date().getTime();
      passedTime = end - start;
      start = end;
    }

    log.info(key + " executed in: " + passedTime + " ms");

  }
}
