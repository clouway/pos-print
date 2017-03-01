package com.evo.servicebroker.client;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class ServiceBrokerFixingTest {
  ServiceBrokerClient client = ServiceBrokerClientFactory.getServiceForServer("http://localhost:" + 8080 + "");
// ServiceBrokerClient client = ServiceBrokerClientFactory.getServiceForServer("http://sb2.evo.bg:" + 8080 + "");


  @Test
  @Ignore
  public void testNormalBillExecute() {


    JsonSerializer serializer = JsonSerializerBuilder.createSerializer();
    Receipt receipt = (Receipt) serializer.deserializeEntity(this.getClass().getResourceAsStream("receipt.json"), Receipt.class);


    client.print(receipt, Receipt.class);

  }
  @Test
  @Ignore
  public void testMtelBillExecute() {


   JsonSerializer serializer = JsonSerializerBuilder.createSerializer();
    Receipt receipt = (Receipt) serializer.deserializeEntity(this.getClass().getResourceAsStream("mtelPaymetReceipt.json"), Receipt.class);


    client.print(receipt, Receipt.class);

  }


  @Test
  @Ignore
  public void testExecuteDailyReport() throws Exception {

    FinancialReportRequest request = FinancialReportRequest.dailyReport("85.217.129.114", false);

    client.printFinancialReport(request);

  }
  @Test
  @Ignore
  public void testExecuteFinalDailyReport() throws Exception {

    FinancialReportRequest request = FinancialReportRequest.dailyReport("85.217.129.114", true);

    client.printFinancialReport(request);

  }

  @Test
  @Ignore
  public void testExecutePeriodReport() throws Exception {

    FinancialReportRequest request = FinancialReportRequest.periodReport("85.217.129.114", date(2011, 12, 1), date(2012, 1, 31));

    client.printFinancialReport(request);

  }

  public Date date(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    return calendar.getTime();
  }
}
