package com.clouway.pos.print.adapter.db

import com.clouway.pos.print.core.*
import com.google.inject.Inject
import com.google.inject.Provider
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.Updates.set
import org.bson.Document
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Optional

/**
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
class PersistentReceiptRepository @Inject constructor(private val database: Provider<MongoDatabase>,
                                                      private val printingListener: PrintingListener,
                                                      private val idGenerator: IdGenerator)
  : ReceiptRepository {
  private val collectionName: String = "receipts"

  /**
   * Creates an index on the requestId, isFiscal and receiptId fields.
   */
  init {
    database.get().getCollection(collectionName)
      .createIndex(Indexes.ascending("requestId", "receiptId", "isFiscal"))
  }

  override fun register(receiptRequest: PrintReceiptRequest): ReceiptWithStatus {
    val receipt = receiptRequest.receipt

    if (receipts()
        .find(and(
          eq("receipt.receiptId", receipt.receiptId),
          eq("isFiscal", receiptRequest.isFiscal))).any())
      throw ReceiptAlreadyRegisteredException()

    val requestId = idGenerator.newId()

    val receiptWithStatus = ReceiptWithStatus(
      requestId,
      receipt,
      receiptRequest.operatorId,
      receiptRequest.sourceIp,
      receiptRequest.isFiscal,
      PrintStatus.PRINTING,
      LocalDateTime.now().toInstant(ZoneOffset.UTC).epochSecond
    )

    val receiptWithStatusDoc = receiptWithStatus.toDocument()

    receipts().insertOne(receiptWithStatusDoc)

    return receiptWithStatus
  }

  override fun getByRequestId(requestId: String): Optional<ReceiptWithStatus> {
    val receiptDoc = receipts().find(and(
      eq("requestId", requestId))).firstOrNull()
      ?: return Optional.empty()

    return Optional.of(receiptDoc.toReceiptWithStatus())
  }

  override fun getLatest(limit: Int): List<ReceiptWithStatus> {
    val receiptList = mutableListOf<ReceiptWithStatus>()

    receipts().find()
      .sort(Document("creationSeconds", -1))
      .limit(limit)
      .toList()
      .forEach {
      receiptList.add(it.toReceiptWithStatus())
    }

    return receiptList
  }

  override fun getStatus(receiptRequestId: String): PrintStatus {
    val receiptDoc = receipts().find(eq("requestId", receiptRequestId)).firstOrNull()
      ?: throw ReceiptNotRegisteredException()

    return PrintStatus.valueOf(receiptDoc.getString("printStatus"))
  }

  override fun finishPrinting(requestId: String): ReceiptWithStatus {
    val receiptDoc = receipts().findOneAndUpdate(
      eq("requestId", requestId),
      set("printStatus", PrintStatus.PRINTED.name))
      ?: throw ReceiptNotRegisteredException()

    printingListener.onPrinted(receiptDoc.get("receipt", Document::class.java).toReceipt(), PrintStatus.PRINTED)

    return receiptDoc.toReceiptWithStatus()
  }

  override fun failPrinting(requestId: String): ReceiptWithStatus {
    val receiptDoc = receipts().findOneAndUpdate(
      eq("requestId", requestId),
      set("printStatus", PrintStatus.FAILED.name))
      ?: throw ReceiptNotRegisteredException()

    printingListener.onPrinted(receiptDoc.get("receipt", Document::class.java).toReceipt(), PrintStatus.FAILED)

    return receiptDoc.toReceiptWithStatus()
  }

  private fun receipts(): MongoCollection<Document> {
    return database.get().getCollection(collectionName)
  }

  private fun ReceiptWithStatus.toDocument(): Document {
    val receiptDoc = this.receipt.toDocument()

    val receiptWithStatusDoc = Document()

    receiptWithStatusDoc.append("receipt", receiptDoc)
    receiptWithStatusDoc["requestId"] = this.requestId
    receiptWithStatusDoc["sourceIp"] = this.sourceIp
    receiptWithStatusDoc["operatorId"] = this.operatorId
    receiptWithStatusDoc["isFiscal"] = this.isFiscal
    receiptWithStatusDoc["printStatus"] = this.printStatus.name
    receiptWithStatusDoc["creationSecond"] =  this.creationSecond

    return receiptWithStatusDoc
  }


  private fun Document.toReceiptWithStatus(): ReceiptWithStatus {
    return ReceiptWithStatus(
      this.getString("requestId"),
      (this["receipt"] as Document).toReceipt(),
      this.getString("operatorId"),
      this.getString("sourceIp"),
      this.getBoolean("isFiscal"),
      PrintStatus.valueOf(this.getString("printStatus")),
      this.getLong("creationSecond")
    )
  }

  @Suppress("UNCHECKED_CAST")
  private fun Document.toReceipt(): Receipt {
    val receipt = Receipt.Builder()
      .withReceiptId(this.getString("receiptId"))
      .withAmount(this.getDouble("amount"))
      .currency(this.getString("currency"))
      .prefixLines(this["prefixLines"] as List<String>)
      .suffixLines(this["suffixLines"] as List<String>)

    val itemList = this["receiptItems"] as List<Document>

    itemList.forEach {
      receipt.addItem(it.toReceiptItem())
    }

    return receipt.build()
  }

  private fun Receipt.toDocument(): Document {
    val itemList = this.receiptItems

    val docList = mutableListOf<Document>()

    itemList.forEach {
      docList.add(it.toDocument())
    }

    return Document()
      .append("receiptId", this.receiptId)
      .append("amount", this.amount)
      .append("prefixLines", this.prefixLines())
      .append("suffixLines", this.suffixLines())
      .append("currency", this.currency)
      .append("receiptItems", docList)
  }

  private fun Document.toReceiptItem(): ReceiptItem {
    return ReceiptItem.newItem()
      .name(this.getString("name"))
      .price(this.getDouble("price"))
      .quantity(this.getDouble("quantity"))
      .build()
  }

  private fun ReceiptItem.toDocument(): Document {
    return Document()
      .append("name", this.name)
      .append("price", this.price)
      .append("quantity", this.quantity)
  }
}