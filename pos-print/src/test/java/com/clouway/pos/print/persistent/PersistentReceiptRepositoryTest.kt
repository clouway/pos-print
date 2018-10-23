package com.clouway.pos.print.persistent

import com.clouway.pos.print.adapter.db.PersistentReceiptRepository
import com.clouway.pos.print.core.IdGenerator
import com.clouway.pos.print.core.PrintReceiptRequest
import com.clouway.pos.print.core.PrintStatus
import com.clouway.pos.print.core.PrintingListener
import com.clouway.pos.print.core.Receipt
import com.clouway.pos.print.core.ReceiptAlreadyRegisteredException
import com.clouway.pos.print.core.ReceiptNotRegisteredException
import com.google.inject.util.Providers
import org.jmock.AbstractExpectations.returnValue
import org.jmock.Expectations
import org.jmock.Mockery
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Assert.assertThat
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
class PersistentReceiptRepositoryTest {
  companion object {
    @ClassRule
    @JvmField
    val dataStoreRule = DatastoreRule()
  }

  @Rule
  @JvmField
  val context: JUnitRuleMockery = JUnitRuleMockery()

  @Rule
  @JvmField
  var cleaner = DatastoreCleaner(dataStoreRule.db())

  private val idGenerator = context.mock(IdGenerator::class.java)

  private val printingListener = context.mock(PrintingListener::class.java)

  private val repository = PersistentReceiptRepository(Providers.of(dataStoreRule.db()),
    printingListener, idGenerator)

  private val receiptId = "::receiptId::"
  private val requestId = "::requestId::"

  private val receipt = Receipt.newReceipt().withReceiptId(receiptId).build()

  private val sourceIp = "::sourceIp::"
  private val operatorId = "::operatorId::"

  private val fiscalReceiptRequest = PrintReceiptRequest(receipt, sourceIp, operatorId, true)
  private val nonFiscalReceiptRequest = PrintReceiptRequest(receipt, sourceIp, operatorId, false)

  @Test
  fun happyPath() {
    context.expecting {
      allowing(idGenerator).newId()
      will(returnValue(requestId))
    }

    val fiscalReceiptWithStatus = repository.register(fiscalReceiptRequest)
    val nonFiscalReceiptWithStatus = repository.register(nonFiscalReceiptRequest)

    assertThat(repository.getStatus(fiscalReceiptWithStatus.requestId), Is(PrintStatus.PRINTING))
    assertThat(repository.getStatus(nonFiscalReceiptWithStatus.requestId), Is(PrintStatus.PRINTING))
  }

  @Test(expected = ReceiptAlreadyRegisteredException::class)
  fun registeringSameReceiptThrowsException() {
    context.expecting {
      allowing(idGenerator).newId()
      will(returnValue(requestId))
    }

    repository.register(fiscalReceiptRequest)
    repository.register(fiscalReceiptRequest)
  }

  @Test
  fun getReceiptById() {
    context.expecting {
      allowing(idGenerator).newId()
      will(returnValue(requestId))
    }

    val receiptWithStatus = repository.register(fiscalReceiptRequest)

    val retrievedReceipt = repository.getByRequestId(receiptWithStatus.requestId)

    assertThat(retrievedReceipt.isPresent, Is(true))
    assertThat(retrievedReceipt.get(), Is(receiptWithStatus))
  }

  @Test
  fun getReceiptByIdReturnsEmptyWhenNotFound() {
    val retrievedReceipt = repository.getByRequestId("::non-existent-receipt::")

    assertThat(retrievedReceipt.isPresent, Is(false))
  }

  @Test
  fun getLastReceipts() {
    context.expecting {
      allowing(idGenerator).newId()
      will(returnValue(requestId))
    }

    val nonFiscalReceiptWithStatus = repository.register(nonFiscalReceiptRequest)
    val fiscalReceiptWithStatus = repository.register(fiscalReceiptRequest)

    val lastReceipts = repository.getLatest(2)

    assertThat(lastReceipts, Is(listOf(nonFiscalReceiptWithStatus, fiscalReceiptWithStatus)))
  }

  @Test
  fun getReceiptStatus() {
    context.expecting {
      allowing(idGenerator).newId()
      will(returnValue(requestId))
    }

    val receiptWithStatus = repository.register(fiscalReceiptRequest)

    assertThat(repository.getStatus(receiptWithStatus.requestId), Is(receiptWithStatus.printStatus))
  }

  @Test(expected = ReceiptNotRegisteredException::class)
  fun gettingStatusOfNonExistingReceiptThrowsException() {
    repository.getStatus("::non-existent-receiptId::")
  }

  @Test
  fun finishPrintingReceipt() {
    context.expecting {
      allowing(idGenerator).newId()
      will(returnValue(requestId))

      oneOf(printingListener).onPrinted(receipt, PrintStatus.PRINTED)
    }

    val receiptWithStatus = repository.register(fiscalReceiptRequest)
    val finishedReceipt = repository.finishPrinting(receiptWithStatus.requestId)

    assertThat(repository.getStatus(receiptWithStatus.requestId), Is(PrintStatus.PRINTED))
    assertThat(finishedReceipt, Is(receiptWithStatus))
  }

  @Test(expected = ReceiptNotRegisteredException::class)
  fun finishingNonExistingReceiptThrowsException() {
    repository.finishPrinting("::fake-receiptId::")
  }

  @Test
  fun rejectPrintingReceipt() {
    context.expecting {
      allowing(idGenerator).newId()
      will(returnValue(requestId))

      oneOf(printingListener).onPrinted(receipt, PrintStatus.FAILED)
    }

    val receiptWithStatus = repository.register(fiscalReceiptRequest)
    val failedReceipt = repository.failPrinting(receiptWithStatus.requestId)

    assertThat(repository.getStatus(receiptWithStatus.requestId), Is(PrintStatus.FAILED))
    assertThat(failedReceipt, Is(receiptWithStatus))
  }

  @Test(expected = ReceiptNotRegisteredException::class)
  fun rejectingNonExistingReceiptThrowsException() {
    repository.finishPrinting("::fake-receiptId::")
  }

  private fun Mockery.expecting(block: Expectations.() -> Unit) {
    checking(Expectations().apply(block))
  }
}