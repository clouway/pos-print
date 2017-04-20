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
   *
   * @param record The device to be registered
   * @return The id of the registered device
   */
  @Throws(DeviceAlreadyExistException::class)
  fun register(record: CashRegister): String

  /**
   * Retrieves all records from the DB
   *
   * @return List of all registered devices
   */
  fun getAll(): List<CashRegister>

  /**
   * Get cash register by sourceIp
   *
   * @param sourceIp Used to search for the specific device
   * @return The device with the corresponding sourceIp
   */
  fun getBySourceIp(sourceIp: String): Optional<CashRegister>

  /**
   * Deletes the device with the corresponding id
   *
   * @param id Used to search for the device to be deleted
   * @return The id of the deleted device
   */
  @Throws(DeviceDoesNotExistException::class)
  fun delete(id: String): String
}

internal class DeviceAlreadyExistException : Throwable()
internal class DeviceDoesNotExistException : Throwable()