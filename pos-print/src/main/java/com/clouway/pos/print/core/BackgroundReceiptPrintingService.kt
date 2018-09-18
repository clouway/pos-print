package com.clouway.pos.print.core

import com.clouway.pos.print.printer.Status
import com.google.common.util.concurrent.AbstractExecutionThreadService
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.io.IOException

/**
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
class BackgroundReceiptPrintingService @Inject constructor(private var repository: ReceiptRepository,
                                                           private var factory: PrinterFactory,
                                                           private var queue: PrintQueue)
  : AbstractExecutionThreadService() {

  override fun run() {
      printReceipts()
  }

  private val logger = LoggerFactory.getLogger(BackgroundReceiptPrintingService::class.java)

  fun printReceipts() {
    var nextReceiptRequest: ReceiptWithStatus? = queue.next()

    while (nextReceiptRequest != null) {

      val receipt = nextReceiptRequest.receipt

      var printer: ReceiptPrinter? = null

      try {
        printer = factory.getPrinter(nextReceiptRequest.sourceIp)

        val printResponse = if (nextReceiptRequest.isFiscal) printer.printFiscalReceipt(receipt)
        else printer.printReceipt(receipt)

        if (printResponse.warnings.contains(Status.FISCAL_RECEIPT_IS_OPEN)
          || printResponse.warnings.contains(Status.NON_FISCAL_RECEIPT_IS_OPEN)) {
          logger.info("Receipt printing accepted")
          logger.info(printResponse.warnings.toString())
          repository.finishPrinting(nextReceiptRequest.requestId)
        } else {
          logger.info("Receipt printing rejected")
          logger.info(printResponse.warnings.toString())
          repository.failPrinting(nextReceiptRequest.requestId)
        }


      }catch (ex: ReceiptNotRegisteredException) {
        logger.warn("Receipt with request id ${nextReceiptRequest.requestId} was not found in queue")
      }catch (ex: DeviceNotFoundException) {
        logger.warn("Device was not found")
        repository.failPrinting(nextReceiptRequest.requestId)
      }catch (ex: IOException){
        logger.warn("Printer threw IO Exception")
        repository.failPrinting(nextReceiptRequest.requestId)
      }finally {
        printer?.close()
        nextReceiptRequest = queue.next()
      }
    }
  }

  override fun startUp() {
    logger.info("Starting background printing service")

    super.startUp()
  }

  override fun triggerShutdown() {
    logger.info("Stopping background printing service")

    super.triggerShutdown()
  }
}
