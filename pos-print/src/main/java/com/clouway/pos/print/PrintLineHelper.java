package com.clouway.pos.print;

import java.util.Date;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public interface PrintLineHelper {
  String customer(String customer);

  String address(String address);

  String contractNoAndDate(String contractNumber, Date date);

  String line();

  String transactionNo();

  String cashier(String cashierName);

  String period(Date start, Date end);

  String charge(Double amount, Double quantity);

  String total(Double totalAmount);

  String itemSum(Double sum);

  String inCash(Double total);

  String justify(String left, String right);

  String justify(String left, String right, char field);
}
