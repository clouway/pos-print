package com.clouway.pos.print.adapter.db

import com.clouway.pos.print.core.IdGenerator
import com.clouway.pos.print.core.ReceiptRepository
import com.clouway.pos.print.core.SimpleUUIDGenerator
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class PersistentModule(private val client: MongoClient, private val databaseName: String) : AbstractModule() {

  override fun configure() {
    bind(CashRegisterRepository::class.java).to(PersistentCashRegisterRepository::class.java).`in`(Singleton::class.java)
    bind(ReceiptRepository::class.java).to(PersistentReceiptRepository::class.java).`in`(Singleton::class.java)
    bind(IdGenerator::class.java).to(SimpleUUIDGenerator::class.java).`in`(Singleton::class.java)
  }

  @Provides
  fun getProvisioningDatabase(): MongoDatabase {
    return client.getDatabase(databaseName)
  }
}