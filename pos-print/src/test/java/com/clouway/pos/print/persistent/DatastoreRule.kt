package com.clouway.pos.print.persistent

import com.github.fakemongo.Fongo
import com.mongodb.client.MongoDatabase
import org.junit.rules.ExternalResource

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class DatastoreRule : ExternalResource() {

  private var db: MongoDatabase? = null

  @Throws(Throwable::class)
  override fun before() {
    db = fongo.getDatabase("pos_test")
  }

  fun db(): MongoDatabase? {
    return db
  }

  companion object {
    private val fongo = Fongo("Datastore Test")
  }
}