package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.ReplyMatchers.Companion.contains
import com.clouway.pos.print.ReplyMatchers.Companion.isNotFound
import com.clouway.pos.print.ReplyMatchers.Companion.isOk
import com.clouway.pos.print.ReplyMatchers.Companion.isStatus
import com.clouway.pos.print.SiteBricksRequestMockery
import com.clouway.pos.print.core.*
import com.clouway.pos.print.printer.Status
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
    val warnings = setOf<Status>(Status.NON_FISCAL_RECEIPT_IS_OPEN)
    val dto: PrintService.PrintReceiptResponseDTO = PrintService.PrintReceiptResponseDTO(warnings)

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("sourceIp")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).printReceipt(anyReceipt)
        will(AbstractExpectations.returnValue(PrintReceiptResponse(warnings)))
        oneOf(printer).close()
      }
    })

    val reply = service.printReceipt(request.mockRequest(receiptDto))
    assertThat(reply, isOk)
    assertThat(reply, contains(dto))
  }

  @Test
  fun printFiscalReceipt() {
    val receiptDto = PrintService.ReceiptDTO("sourceIp", "::any ip::", true)
    val anyReceipt = Receipt.newReceipt().build()
    val warnings = setOf<Status>(Status.FISCAL_RECEIPT_IS_OPEN)
    val dto: PrintService.PrintReceiptResponseDTO = PrintService.PrintReceiptResponseDTO(warnings)

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("sourceIp")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).printFiscalReceipt(anyReceipt)
        will(AbstractExpectations.returnValue(PrintReceiptResponse(warnings)))
        oneOf(printer).close()
      }
    })

    val reply = service.printReceipt(request.mockRequest(receiptDto))
    assertThat(reply, isOk)
    assertThat(reply, contains(dto))
  }

  @Test
  fun receiptDidNotOpened() {
    val receiptDto = PrintService.ReceiptDTO("sourceIp", "::any ip::", false)
    val anyReceipt = Receipt.newReceipt().build()
    val warnings = setOf<Status>(Status.END_OF_PAPER)
    val dto: PrintService.PrintReceiptResponseDTO = PrintService.PrintReceiptResponseDTO(warnings)

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("sourceIp")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).printReceipt(anyReceipt)
        will(AbstractExpectations.returnValue(PrintReceiptResponse(warnings)))
        oneOf(printer).close()
      }
    })

    val reply = service.printReceipt(request.mockRequest(receiptDto))
    assertThat(reply, isStatus(400))
    assertThat(reply, contains(dto))
  }

  @Test
  fun fiscalReceiptDidNotOpened() {
    val receiptDto = PrintService.ReceiptDTO("sourceIp", "::any ip::", true)
    val anyReceipt = Receipt.newReceipt().build()
    val warnings = setOf<Status>(Status.END_OF_PAPER)
    val dto: PrintService.PrintReceiptResponseDTO = PrintService.PrintReceiptResponseDTO(warnings)

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("sourceIp")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).printFiscalReceipt(anyReceipt)
        will(AbstractExpectations.returnValue(PrintReceiptResponse(warnings)))
        oneOf(printer).close()
      }
    })

    val reply = service.printReceipt(request.mockRequest(receiptDto))
    assertThat(reply, isStatus(400))
    assertThat(reply, contains(dto))
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
    assertThat(reply, isNotFound)
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

  @Test
  fun printerRequestTimeout() {
    val receiptDto = PrintService.ReceiptDTO("sourceIp", "::any ip::", false)
    val anyReceipt = Receipt.newReceipt().build()

    context.checking(object : Expectations() {
      init {
        oneOf(factory).getPrinter("sourceIp")
        will(AbstractExpectations.returnValue(printer))
        oneOf(printer).printReceipt(anyReceipt)
        will(AbstractExpectations.throwException(RequestTimeoutException("")))
        oneOf(printer).close()
      }
    })

    val reply = service.printReceipt(request.mockRequest(receiptDto))
    assertThat(reply, isStatus(504))
  }
}