package com.clouway.pos.print.persistent

import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.junit.rules.ExternalResource

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class DatastoreCleaner(private val db: MongoDatabase?) : ExternalResource() {

  @Throws(Throwable::class)
  override fun before() {
    if (db != null) {
      db.listCollections()
        .map { it.getString("name") }
        .filterNot { it.contains("system.") }
        .map { db.getCollection(it) }
        .forEach { it.deleteMany(Document()) }
    }
  }
}