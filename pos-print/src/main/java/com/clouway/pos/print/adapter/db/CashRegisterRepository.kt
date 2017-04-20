package com.clouway.pos.print.adapter.db

import com.clouway.pos.print.core.CashRegister
import java.util.*

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
  @Throws(DeviceAlreadyExistException::class)
  fun register(record: CashRegister): String

  /**
   * Retrieves all records from the DB
   */
  fun getAll(): List<CashRegister>

  /**
   * Get cash register by sourceIp
   */
  fun getBySourceIp(sourceIp: String): Optional<CashRegister>
}

internal class DeviceAlreadyExistException : Throwable()