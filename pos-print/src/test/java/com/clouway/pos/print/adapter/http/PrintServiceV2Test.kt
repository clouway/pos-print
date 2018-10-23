package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.FakeRequest
import com.clouway.pos.print.ReplyMatchers
import com.clouway.pos.print.core.PrintQueue
import com.clouway.pos.print.core.PrintReceiptRequest
import com.clouway.pos.print.core.PrintStatus
import com.clouway.pos.print.core.Receipt
import com.clouway.pos.print.core.ReceiptAlreadyRegisteredException
import com.clouway.pos.print.core.ReceiptNotRegisteredException
import com.clouway.pos.print.core.ReceiptRepository
import com.clouway.pos.print.core.ReceiptWithStatus
import org.hamcrest.MatcherAssert
import org.jmock.AbstractExpectations.returnValue
import org.jmock.AbstractExpectations.throwException
import org.jmock.Expectations
import org.jmock.Mockery
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Optional

/**
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
class PrintServiceV2Test {

  @Rule
  @JvmField
  var context = JUnitRuleMockery()

  private fun Mockery.expecting(block: Expectations.() -> Unit) {
    checking(Expectations().apply(block))
  }

  private val repo = context.mock(ReceiptRepository::class.java)
  private val queue = context.mock(PrintQueue::class.java)

  private val receipt = Receipt.newReceipt().withReceiptId("::receipt-id::").build()

  private val sourceIp = "::sourceIp::"
  private val operatorId = "::operatorId::"

  private val fiscalReceiptDTO = PrintServiceV2
    .ReceiptDTO(sourceIp, operatorId, true, receipt = receipt)
  private val nonFiscalReceiptDTO = PrintServiceV2
    .ReceiptDTO(sourceIp, operatorId, false, receipt = receipt)

  private val fiscalRequestId = "::fiscal-request-id::"
  private val nonFiscalRequestId = "::non-fiscal-request-id::"
  private val instant = LocalDateTime.now().toInstant(ZoneOffset.UTC).epochSecond

  private val fiscalReceiptRequest = PrintReceiptRequest(receipt, sourceIp, operatorId, true)
  private val fiscalReceiptWithStatus = ReceiptWithStatus(fiscalRequestId, receipt, operatorId, sourceIp, true, PrintStatus.PRINTING, instant)

  private val nonFiscalReceiptRequest = PrintReceiptRequest(receipt, sourceIp, operatorId, false)
  private val nonFiscalReceiptWithStatus = ReceiptWithStatus(nonFiscalRequestId, receipt, operatorId, sourceIp, false, PrintStatus.PRINTING, instant)

  private val service = PrintServiceV2(repo, queue)

  @Test
  fun registerReceiptsForPrinting() {
    context.expecting {
      oneOf(repo).register(fiscalReceiptRequest)
      will(returnValue(fiscalReceiptWithStatus))

      oneOf(repo).register(nonFiscalReceiptRequest)
      will(returnValue(nonFiscalReceiptWithStatus))

      oneOf(queue).queue(fiscalReceiptWithStatus)
      oneOf(queue).queue(nonFiscalReceiptWithStatus)
    }

    val fiscalReply = service.printReceipt(FakeRequest.newRequest(fiscalReceiptDTO))
    val nonFiscalReply = service.printReceipt(FakeRequest.newRequest(nonFiscalReceiptDTO))

    MatcherAssert.assertThat(fiscalReply, ReplyMatchers.isAccepted)
    MatcherAssert.assertThat(fiscalReply, ReplyMatchers.contains(fiscalRequestId))
    MatcherAssert.assertThat(nonFiscalReply, ReplyMatchers.isAccepted)
    MatcherAssert.assertThat(nonFiscalReply, ReplyMatchers.contains(nonFiscalRequestId))
  }

  @Test
  fun savingAlreadyExistingReceiptThrowsException() {
    context.expecting {
      oneOf(repo).register(fiscalReceiptRequest)
      will(throwException(ReceiptAlreadyRegisteredException()))
    }

    val reply = service.printReceipt(FakeRequest.newRequest(fiscalReceiptDTO))
    MatcherAssert.assertThat(reply, ReplyMatchers.isBadRequest)
  }

  @Test
  fun getReceiptStatus() {
    context.expecting {
      oneOf(repo).getStatus(receipt.receiptId)
      will(returnValue(PrintStatus.PRINTING))
    }

    val reply = service.getReceiptStatus(receipt.receiptId)
    MatcherAssert.assertThat(reply, ReplyMatchers.isOk)
    MatcherAssert.assertThat(reply, ReplyMatchers.contains(PrintStatus.PRINTING))
  }

  @Test
  fun getLatestReceipts() {
    context.expecting {
      oneOf(repo).getLatest(1)
      will(returnValue(listOf(fiscalReceiptWithStatus)))
    }

    val reply = service.getLatestReceipts(1)
    MatcherAssert.assertThat(reply, ReplyMatchers.isOk)
    MatcherAssert.assertThat(reply, ReplyMatchers
      .contains(listOf(fiscalReceiptWithStatus)))
  }

  @Test
  fun requeueReceiptByRequestId(){
    context.expecting {
      oneOf(repo).getByRequestId("::request-id::")
      will(returnValue(Optional.of(fiscalReceiptWithStatus)))

      oneOf(queue).queue(fiscalReceiptWithStatus)
    }

    val reply = service.requeueReceipt("::request-id::")
    MatcherAssert.assertThat(reply, ReplyMatchers.isOk)
  }

  @Test
  fun gettingReceiptStatusOfNonExistingReturnsNotFound() {
    context.expecting {
      oneOf(repo).getStatus(receipt.receiptId)
      will(throwException(ReceiptNotRegisteredException()))
    }

    val reply = service.getReceiptStatus(receipt.receiptId)
    MatcherAssert.assertThat(reply, ReplyMatchers.isNotFound)
  }
}