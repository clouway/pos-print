package com.clouway.pos.print.persistent

import com.clouway.pos.print.core.CashRegister

/**
 * Provides the methods to be implemented for work with
 * devices collection in the database
 *
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
interface CashRegisterRepository {

  /**
   * Registers a single record
   */
  @Throws (DeviceAlreadyExistException::class)
  fun register(record: CashRegister): String

  /**
   * Retrieves all records from the DB
   */
  fun getAll(): List<CashRegister>
}

internal class DeviceAlreadyExistException : Throwable()