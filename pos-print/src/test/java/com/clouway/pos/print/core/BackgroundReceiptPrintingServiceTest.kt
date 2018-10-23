package com.clouway.pos.print.core

import com.clouway.pos.print.persistent.DatastoreCleaner
import com.clouway.pos.print.persistent.DatastoreRule
import com.clouway.pos.print.printer.Status
import org.jmock.AbstractExpectations.returnValue
import org.jmock.AbstractExpectations.throwException
import org.jmock.Expectations
import org.jmock.Mockery
import org.jmock.integration.junit4.JUnitRuleMockery
import org.jmock.lib.concurrent.Synchroniser
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
class BackgroundReceiptPrintingServiceTest {

  @Rule
  @JvmField
  var context = JUnitRuleMockery()

  init {
    context.setThreadingPolicy(Synchroniser())
  }

  @Rule
  @JvmField
  val dataStoreRule = DatastoreRule()

  @Rule
  @JvmField
  var cleaner = DatastoreCleaner(dataStoreRule.db())

  private val repo = context.mock(ReceiptRepository::class.java)
  private val factory = context.mock(PrinterFactory::class.java)
  private val queue = context.mock(PrintQueue::class.java)

  private val service = BackgroundReceiptPrintingService(repo, factory, queue)

  private val printer = context.mock(ReceiptPrinter::class.java)

  private val receipt = Receipt.newReceipt()
    .withReceiptId("::receiptId::")
    .withAmount(200.0)
    .build()

  private val requestId = "::requestId::"
  private val sourceIp = "::sourceIp::"
  private val operatorId = "::operatorId::"
  private val isFiscal = true
  private val instant = LocalDateTime.now().toInstant(ZoneOffset.UTC).epochSecond

  private val fiscalReceiptWithStatus = ReceiptWithStatus(requestId, receipt, operatorId, sourceIp, isFiscal, PrintStatus.PRINTING, instant)

  private val acceptedPrintingResponse = PrintReceiptResponse(setOf(Status.FISCAL_RECEIPT_IS_OPEN))
  private val rejectedPrintingResponse = PrintReceiptResponse(setOf(Status.BROKEN_PRINTIN_MECHANISM))

  @Test
  fun printReceipt() {
    context.expecting {
      oneOf(queue).next()
      will(returnValue(fiscalReceiptWithStatus))

      oneOf(factory).getPrinter(sourceIp)
      will(returnValue(printer))

      oneOf(printer).printFiscalReceipt(receipt)
      will(returnValue(acceptedPrintingResponse))

      oneOf(repo).finishPrinting(requestId)
      will(returnValue(fiscalReceiptWithStatus))

      oneOf(printer).close()

      oneOf(queue).next()
      will(returnValue(null))
    }

    service.printReceipts()
  }

  @Test
  fun failPrintingReceiptWhenNotPrinted() {
    context.expecting {
      oneOf(queue).next()
      will(returnValue(fiscalReceiptWithStatus))

      oneOf(factory).getPrinter(sourceIp)
      will(returnValue(printer))

      oneOf(printer).printFiscalReceipt(receipt)
      will(returnValue(rejectedPrintingResponse))

      oneOf(repo).failPrinting(requestId)
      will(returnValue(fiscalReceiptWithStatus))

      oneOf(printer).close()

      oneOf(queue).next()
      will(returnValue(null))
    }

    service.printReceipts()
  }

  @Test
  fun failPrintingWhenDeviceNotFound() {
    context.expecting {
      oneOf(queue).next()
      will(returnValue(fiscalReceiptWithStatus))

      oneOf(factory).getPrinter(sourceIp)
      will(throwException(DeviceNotFoundException()))

      oneOf(repo).failPrinting(requestId)
      will(returnValue(fiscalReceiptWithStatus))

      oneOf(queue).next()
      will(returnValue(null))
    }

    service.printReceipts()
  }

  @Test
  fun failPrintingWithIOException() {
    context.expecting {
      oneOf(queue).next()
      will(returnValue(fiscalReceiptWithStatus))

      oneOf(factory).getPrinter(sourceIp)
      will(throwException(IOException()))

      oneOf(repo).failPrinting(requestId)
      will(returnValue(fiscalReceiptWithStatus))

      oneOf(queue).next()
      will(returnValue(null))
    }

    service.printReceipts()
  }

  private fun Mockery.expecting(block: Expectations.() -> Unit) {
    checking(Expectations().apply(block))
  }
}