package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.ReplyMatchers.Companion.contains
import com.clouway.pos.print.ReplyMatchers.Companion.isBadRequest
import com.clouway.pos.print.ReplyMatchers.Companion.isOk
import com.clouway.pos.print.ReplyMatchers.Companion.isStatus
import com.clouway.pos.print.SiteBricksRequestMockery
import com.clouway.pos.print.core.*
import org.hamcrest.MatcherAssert.assertThat
import org.jmock.AbstractExpectations
import org.jmock.Expectations
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
class PrintServiceTest {

  @Rule @JvmField
  var context = JUnitRuleMockery()

  private val factory = context.mock(PrinterFactory::class.java)
  private val printer = context.mock(ReceiptPrinter::class.java)

  private val request = SiteBricksRequestMockery()
  private val service = PrintService(factory)

  @Test
  fun printReceipt() {
    val receiptDto = PrintService.ReceiptDTO("sourceIp", "::any ip::", false)
    val anyReceipt = Receipt.newReceipt().build()

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("sourceIp")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).printReceipt(anyReceipt)
        will(AbstractExpectations.returnValue(PrintReceiptResponse(emptySet())))
        oneOf(printer).close()
      }
    })

    val reply = service.printReceipt(request.mockRequest(receiptDto))
    assertThat(reply, isOk)
  }

  @Test
  fun printFiscalReceipt() {
    val receiptDto = PrintService.ReceiptDTO("sourceIp", "::any ip::", true)
    val anyReceipt = Receipt.newReceipt().build()

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("sourceIp")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).printFiscalReceipt(anyReceipt)
        will(AbstractExpectations.returnValue(PrintReceiptResponse(emptySet())))
        oneOf(printer).close()
      }
    })

    val reply = service.printReceipt(request.mockRequest(receiptDto))
    assertThat(reply, isOk)
  }

  @Test
  fun unknownCashRegister() {
    val receiptDto = PrintService.ReceiptDTO("::any ip::", "::any ip::", true)

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(DeviceNotFoundException()))
      }
    })

    val reply = service.printReceipt(request.mockRequest(receiptDto))
    assertThat(reply, isBadRequest)
    assertThat(reply, contains(ErrorResponse("Device not found.")))
  }

  @Test
  fun connectionCannotBeEstablished() {
    val receiptDto = PrintService.ReceiptDTO("::any ip::", "::any ip::", true)

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("::any ip::")
        will(AbstractExpectations.throwException(IOException()))
      }
    })

    val reply = service.printReceipt(request.mockRequest(receiptDto))
    assertThat(reply, isStatus(480))
    assertThat(reply, contains(ErrorResponse("Device can't connect.")))
  }
}