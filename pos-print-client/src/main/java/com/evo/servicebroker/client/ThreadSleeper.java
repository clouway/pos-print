package com.evo.servicebroker.client;

import java.util.logging.Logger;

/**
 * ThreadSleeper is a Sleeper class that is using forcing sleeper to use
 * the current thread to sleep for a given interval of time.
 * 
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class ThreadSleeper implements Sleeper {
  public final Logger log = Logger.getLogger(ThreadSleeper.class.getName());

  public void sleep(Integer time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      log.info(e.getMessage());
      e.printStackTrace();
    }
  }
}
