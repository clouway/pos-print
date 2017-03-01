package com.clouway.servicebroker.learning.test;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class ArrayCheckingTest {

  @Test
  public void testArray() {
    String[] names = new String[] { "1", "2"};
    boolean isArray = names instanceof Object[];
    assertThat(isArray, is(equalTo(true)));

  }

}
