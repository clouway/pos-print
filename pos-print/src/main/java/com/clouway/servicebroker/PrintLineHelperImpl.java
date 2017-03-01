package com.clouway.servicebroker;

import com.clouway.common.DateConverter;
import com.clouway.common.NumberFormat;

import java.util.Date;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
class PrintLineHelperImpl implements PrintLineHelper {
  private final int STRING_LENGTH = 30;
  private final String LINE_SYMBOL = "-";
  private final String CUSTOMER = "Клиент:";
  private final String ADDRESS = "Адрес:";
  private final String CONTRACT_NUMBER_AND_DATA = "Договор No:%s/%s";
  private final String TRANSACTION_NO = "Трансакция No: ";
  private final String CASHIER = "Касиер:";
  private final String PERIOD = "от: %s до: %s";
  private final String TOTAL = "Обща сума:";
  private final String IN_CASH = "В брой";
  private final String QUANTITY = " X ";
  private final String ITEM_SUM = "Сума*Б";
  private final char SMALL_DOTS = (char)127;


  public String customer(String customer) {
    return CUSTOMER + customer;
  }

  public String address(String address) {
    return ADDRESS + address;
  }

  public String contractNoAndDate(String contractNumber, Date date) {
    return String.format(CONTRACT_NUMBER_AND_DATA, contractNumber, DateConverter.convertToDayMonthYear(date));
  }

  public String line() {
    return generateLine();
  }

  public String transactionNo() {
    return TRANSACTION_NO;
  }

  public String cashier(String cashierName) {
    return CASHIER + cashierName;
  }

  public String period(Date start, Date end) {
    return String.format(PERIOD, DateConverter.convertToDayMonthYear(start), DateConverter.convertToDayMonthYear(end));
  }

  public String charge(Double price, Double quantity) {
    return makeQuantity(quantity) + NumberFormat.formatDouble(price);
  }

  public String total(Double totalAmount) {
    return justify(TOTAL, NumberFormat.formatDouble(totalAmount));
  }

  public String itemSum(Double sum) {
    return justify(ITEM_SUM, NumberFormat.formatDouble(sum));
  }

  public String inCash(Double total) {
    return justify(IN_CASH, NumberFormat.formatDouble(total));
  }

  private String generateLine(){
    String line = "";
    for(int i=0; i<STRING_LENGTH; i++){
      line = line + LINE_SYMBOL;
    }

    return line;
  }

  private String makeQuantity(Double quantity){
    String value = NumberFormat.formatDouble(quantity);
//    String[] split = value.split("\\.");
//    if("00".equals(split[1])){
//      value = split[0];
//    }
    return value + "0" + QUANTITY;
  }

  public String justify(String left, String right){
    return justify(left, right, SMALL_DOTS);
  }

  public String justify(String left, String right, char field){
    String line = "";
    int leftLength = left.length();
    int rightLength = right.length();
    int rightAlignSpace = STRING_LENGTH - rightLength;

    for(int i=0; i<STRING_LENGTH; i++){
      if(i < leftLength && i < rightAlignSpace-1){
        line = line + left.charAt(i);
      } else if(i >= rightAlignSpace){
        return line + right;
      } else {
        line = line + field;
      }
    }

    return line;
  }
}
