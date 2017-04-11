package com.clouway.pos.print.persistent

import com.google.inject.AbstractModule
import com.google.inject.Singleton

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class PersistentModule: AbstractModule() {

  override fun configure() {
    bind(CashRegisterRepository::class.java).to(InMemoryCashRegisterRepository::class.java).`in`(Singleton::class.java)
  }
}