package com.clouway.pos.print.printer;


import com.clouway.pos.print.core.RegisterState;
import org.junit.Ignore;
import org.junit.Test;

import java.net.Socket;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static com.clouway.pos.print.client.Receipt.newReceipt;
import static com.clouway.pos.print.client.ReceiptItem.newItem;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
@Ignore
public class FP705PrinterTest {

  @Test
  public void happyPath() throws Exception {
    Socket socket = new Socket("172.16.188.37", 4999);

    FP705Printer printer = new FP705Printer(socket.getInputStream(), socket.getOutputStream());
    String time = printer.getTime();
    System.out.println(time);
  }

  @Test
  public void paperCut() throws Exception {
    Socket socket = new Socket("172.16.188.37", 4999);
    FP705Printer printer = new FP705Printer(socket.getInputStream(), socket.getOutputStream());
    printer.paperCut();
  }

  @Test
  public void printReceiptWithManyItem() throws Exception {
    Socket socket = new Socket("172.16.188.37", 4999);
    socket.setSoTimeout(1000);

    FP705Printer printer = new FP705Printer(socket.getInputStream(), socket.getOutputStream());
    printer.printReceipt(newReceipt().addItems(
            newItem().name("HSI 80/40 - 03/2017").quantity(1d).price(0.10d).build(),
            newItem().name("HSI 80/40 - 04/2017").quantity(1d).price(0.10d).build(),
            newItem().name("HSI 80/40 - 05/2017").quantity(1d).price(0.20d).build()
    ).build());
  }

  @Test
  public void printReceiptWithSingleItem() throws Exception {
    Socket socket = new Socket("172.16.188.37", 4999);
    socket.setSoTimeout(3000);

    FP705Printer printer = new FP705Printer(socket.getInputStream(), socket.getOutputStream());
    printer.printReceipt(newReceipt()
            .currency("BGN")
            .prefixLines(Arrays.asList(
                    "Клиент: Огнян Горанов",
                    "Дата на плащане: 2017-02-03 12:23",
                    "Период: 2017-03 до 2017-05"
            ))
            .suffixLines(Collections.singletonList("Общо: 0.30 BGN"))
            .addItems(
                    newItem().name("Ред 1").quantity(1d).price(0.10d).build(),
                    newItem().name("Ред 2").quantity(1d).price(0.10d).build(),
                    newItem().name("Ред 3").quantity(1d).price(0.10d).build()
            ).build());
  }

  @Test
  public void printReceipt() throws Exception {
    Socket socket = new Socket("172.16.188.37", 4999);
    FP705Printer printer = new FP705Printer(socket.getInputStream(), socket.getOutputStream());

    printer.printFiscalReceipt(
            newReceipt()
                    .prefixLines(Arrays.asList(
                            "Клиент: Огнян Горанов",
                            "Дата на плащане: 2017-02-03 12:23",
                            "Период: 2017-03 до 2017-05"
                    ))
                    .suffixLines(Arrays.asList("Suffix Line 1", "Линия 2"))
                    .addItems(
                            newItem().name("HSI 80/40").quantity(1d).price(0.10d).build(),
                            newItem().name("HSI 80/40 с кирилица").quantity(1d).price(0.10d).build()
                    ).build());
  }

  @Test
  public void testFiscalReportForPeriod() throws Exception {
    Socket socket = new Socket("172.16.188.37", 4999);

    FP705Printer printer = new FP705Printer(socket.getInputStream(), socket.getOutputStream());
    printer.reportForPeriod(LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 10));
  }

  @Test
  public void testFiscalReport() throws Exception {
    Socket socket = new Socket("172.16.188.37", 4999);

    FP705Printer printer = new FP705Printer(socket.getInputStream(), socket.getOutputStream());
    printer.reportForOperator("1", RegisterState.CLEAR);
  }

}