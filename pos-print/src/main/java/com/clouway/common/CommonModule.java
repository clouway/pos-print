package com.clouway.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public class CommonModule extends AbstractModule{
  @Override
  protected void configure() {

  }

  @Provides
  @AllowedAddress
  public Set<String> getAllowedIps(){
    Set<String> address = new HashSet<String>();
    address.add("127.0.0.1");
    
    // local ip addresses
    address.add("85.217.129.100");
    address.add("85.217.129.101");
    address.add("85.217.129.102");
    address.add("85.217.129.103");
    address.add("85.217.129.104");
    address.add("85.217.129.105");
    address.add("85.217.129.106");
    address.add("85.217.129.107");
    address.add("85.217.129.108");
    address.add("85.217.129.109");
    address.add("85.217.129.110");
    address.add("85.217.129.111");
    address.add("85.217.129.112");
    address.add("85.217.129.113");
    address.add("85.217.129.114");
    address.add("85.217.129.115");
    address.add("85.217.129.116");
    address.add("85.217.129.117");
    address.add("85.217.129.118");
    address.add("85.217.129.119");
    address.add("85.217.129.120");
    address.add("85.217.129.121");
    address.add("85.217.129.122");
    address.add("85.217.129.123");
    address.add("85.217.129.124");
    address.add("85.217.129.125");
    address.add("85.217.129.126");
    address.add("85.217.129.127");
    address.add("85.217.129.128");
    address.add("85.217.129.129");
    address.add("85.217.129.130");

    // gorna offices
    address.add("85.217.131.21");
    address.add("85.217.131.27");

    //pavlikeni
    address.add("85.217.191.153");

    //tyrnovo
    address.add("85.217.191.179");
    address.add("85.217.131.17");
    address.add("85.217.191.148");
    address.add("85.217.191.144");
    address.add("85.217.191.154");
    address.add("85.217.130.18");

    //strazica
    address.add("85.217.130.34");

    // current GAE environment
    address.add("74.125.43.141");


    // elena
    address.add("85.217.191.157");
    address.add("87.121.216.181");

    // suhindol
    address.add("85.217.191.175");

    address.add("");
    return address;
  }

  @Provides
  @CurrentDate
  public Date getCurrentDate(){
    return new Date();
  }

  @Provides
  @CurrentDate
  public String getCurrentDateAsString(@CurrentDate Date date){
    return DateConverter.convertToYearMonthDayHourSecond(date);
  }
  

  @Override
  public boolean equals(Object o) {
    return o instanceof CommonModule;
  }

  @Override
  public int hashCode() {
    return CommonModule.class.hashCode();
  }
}
