package com.clouway.pos.print.persistent

import com.clouway.pos.print.core.CashRegister
import com.google.common.collect.Lists
import com.google.common.collect.Maps

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class InMemoryCashRegisterRepository : CashRegisterRepository {
  private var devices = Maps.newHashMap<String, CashRegister>()

  override fun register(record: CashRegister): String {
    val isPresent = devices[record.sourceIp]

    if (isPresent != null) {
      throw DeviceAlreadyExistException()
    }

    devices.put(record.destination, record)

    return record.destination
  }

  override fun getAll(): List<CashRegister> {
    return mapToList(devices)
  }

  private fun mapToList(devices: HashMap<String, CashRegister>): List<CashRegister> {
    val devicesList = Lists.newArrayList<CashRegister>()

    for ((key, value) in devices) {
      devicesList.add(value)
    }

    return devicesList
  }
}
