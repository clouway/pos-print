package com.clouway.pos.print.adapter.db

import com.clouway.pos.print.core.CashRegister
import com.clouway.pos.print.core.FiscalPolicy
import com.google.common.collect.Lists
import com.google.inject.Inject
import com.google.inject.Provider
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.bson.types.ObjectId
import java.util.*

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class PersistentCashRegisterRepository @Inject constructor(private val database: Provider<MongoDatabase>) : CashRegisterRepository {
  override fun register(record: CashRegister): String {
    val result = devices().find(Document().append("sourceIp", record.sourceIp))

    if (result.first() != null) {
      throw DeviceAlreadyExistException()
    }

    val query = adapt(record)
    val registrationResult = devices().updateOne(Document("sourceIp", record.sourceIp), Document("\$set", query), UpdateOptions().upsert(true))

    return registrationResult.upsertedId.asObjectId().value.toHexString()
  }

  override fun getAll(): List<CashRegister> {
    val cashRegisters = Lists.newArrayList<CashRegister>()
    val cursor = devices().find()

    cursor.forEach {
      cashRegisters.add(adapt(it))
    }

    return cashRegisters
  }

  override fun getBySourceIp(sourceIp: String): Optional<CashRegister> {
    val result = devices().find(Document().append("sourceIp", sourceIp))

    if (result.first() == null) {
      return Optional.empty()
    }
    return Optional.of(adapt(result.first()))
  }

  override fun delete(id: String): String {
    devices().findOneAndDelete(Document().append("_id", ObjectId(id))) ?: throw DeviceDoesNotExistException()

    return id
  }

  private fun adapt(record: Document): CashRegister {
    val policy = record["fiscalPolicy"] as Document?
    val policies = when {
        policy == null || policy.isEmpty() -> mutableListOf(FiscalPolicy("1", 20.0))
        else -> policy.keys.map { FiscalPolicy(it, policy[it] as Double) }
    }
    return CashRegister(
      record.getObjectId("_id").toHexString(),
      record.getString("sourceIp"),
      record.getString("destination"),
      record.getString("description"),
      policies
    )
  }

  private fun adapt(record: CashRegister): Document {
    val policy = Document()
    for ((group, vat) in record.fiscalPolicy) {
      policy.put(group.toString(), vat)
    }

    return Document()
      .append("sourceIp", record.sourceIp)
      .append("destination", record.destination)
      .append("description", record.description)
      .append("fiscalPolicy", policy)
  }

  private fun devices(): MongoCollection<Document> {
    return database.get().getCollection("devices")
  }
}
