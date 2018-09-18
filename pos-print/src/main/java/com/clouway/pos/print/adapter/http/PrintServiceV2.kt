package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.core.PrintQueue
import com.clouway.pos.print.core.PrintReceiptRequest
import com.clouway.pos.print.core.Receipt
import com.clouway.pos.print.core.ReceiptAlreadyRegisteredException
import com.clouway.pos.print.core.ReceiptNotRegisteredException
import com.clouway.pos.print.core.ReceiptRepository
import com.clouway.pos.print.transport.GsonTransport
import com.clouway.pos.print.transport.HtmlTransport
import com.google.inject.name.Named
import com.google.sitebricks.At
import com.google.sitebricks.headless.Reply
import com.google.sitebricks.headless.Request
import com.google.sitebricks.headless.Service
import com.google.sitebricks.http.Get
import com.google.sitebricks.http.Post
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

/**
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
@Service
@At("/v2/receipts")
class PrintServiceV2 @Inject constructor(private var repository: ReceiptRepository,
                                         private var queue: PrintQueue) {
  private val logger = LoggerFactory.getLogger(PrintServiceV2::class.java)

  @Post
  @At("/print")
  fun printReceipt(request: Request): Reply<*> {
    val dto = request.read(PrintServiceV2.ReceiptDTO::class.java).`as`(GsonTransport::class.java)

    return try {
      val receiptRequest = PrintReceiptRequest(dto.receipt, dto.sourceIp, dto.operatorId, dto.fiscal)

      val receiptWithStatus = repository.register(receiptRequest)

      queue.queue(receiptWithStatus)

      logger.info("Receipt with request id ${receiptWithStatus.requestId} queued for printing")
      Reply.with(receiptWithStatus.requestId).status(202)
    } catch (ex: ReceiptAlreadyRegisteredException) {
      logger.error("Receipt with id ${dto.receipt.receiptId} and fiscal status ${dto.fiscal} is already registered in queue")
      Reply.saying<Unit>().badRequest()
    }
  }

  @Get
  @At("/:requestId/status")
  fun getReceiptStatus(@Named("requestId") requestId: String): Reply<*> {
    return try {
      val receiptStatus = repository.getStatus(requestId)
      logger.info("Receipt status returned as ${receiptStatus.name}")
      Reply.with(receiptStatus).ok()
    } catch (ex: ReceiptNotRegisteredException) {
      logger.error("Receipt with request id $requestId was  not found")
      Reply.saying<Unit>().notFound()
    }
  }

  @Get
  @At("/view")
  fun viewReceipts(): Reply<*> {
    val htmlPage = File("pos-print/src/main/resources/receipts.html")

    return Reply.with(htmlPage.inputStream()).`as`(HtmlTransport::class.java).ok()
  }

  @Get
  @At("/latest/:limit")
  fun getLatestReceipts(@Named("limit") limit: Int): Reply<*> {
    val receipts = repository.getLatest(limit)

    return Reply.with(receipts).`as`(GsonTransport::class.java).ok()
  }

  @Post
  @At("/:requestId/requeue")
  fun requeueReceipt(@Named("requestId") requestId: String): Reply<*> {
    val receiptPrintRequest = repository.getByRequestId(requestId)
    return if (receiptPrintRequest.isPresent) {
      queue.queue(receiptPrintRequest.get())
      Reply.saying<Unit>().ok()
    } else {
      logger.error("Receipt with request id $requestId was not found")
      Reply.saying<Unit>().notFound()
    }
  }

  internal data class ReceiptDTO(val sourceIp: String = "", val operatorId: String = "", val fiscal: Boolean = false, val receipt: Receipt = Receipt.newReceipt().build())
}