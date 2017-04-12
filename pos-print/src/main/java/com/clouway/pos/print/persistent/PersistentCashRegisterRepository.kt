package com.clouway.pos.print.persistent

import com.clouway.pos.print.core.CashRegister
import com.google.common.collect.Lists
import com.google.inject.Inject
import com.google.inject.Provider
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
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
    devices().insertOne(query)

    return record.destination
  }

  override fun getAll(): List<CashRegister> {
    var cashRegisters = Lists.newArrayList<CashRegister>()
    var cursor = devices().find()

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

  private fun adapt(record: Document): CashRegister {
    return CashRegister(
      record.getString("sourceIp"),
      record.getString("destination"),
      record.getString("description")
    )
  }

  private fun adapt(record: CashRegister): Document {
    return Document()
      .append("sourceIp", record.sourceIp)
      .append("destination", record.destination)
      .append("description", record.description)
  }

  private fun devices(): MongoCollection<Document> {
    return database.get().getCollection("devices")
  }
}
