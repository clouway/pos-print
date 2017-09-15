package com.clouway.pos.print.persistent

import com.clouway.pos.print.adapter.db.DeviceAlreadyExistException
import com.clouway.pos.print.adapter.db.DeviceDoesNotExistException
import com.clouway.pos.print.adapter.db.PersistentCashRegisterRepository
import com.clouway.pos.print.core.CashRegister
import com.clouway.pos.print.core.FiscalPolicy
import com.google.common.collect.Lists
import com.google.inject.util.Providers
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class PersistentCashRegisterRepositoryTest {

  companion object {
    @ClassRule @JvmField
    val dataStoreRule = DatastoreRule()
  }

  @Rule @JvmField
  var cleaner = DatastoreCleaner(dataStoreRule.db())

  private val repository = PersistentCashRegisterRepository(Providers.of(dataStoreRule.db()))

  @Test
  fun happyPath() {
    val id1 = repository.register(CashRegister("", "any ip 1", "any ip 1", "any description", listOf(FiscalPolicy("1", 20.0))))
    val id2 = repository.register(CashRegister("", "any ip 2", "any ip 2", "any description", listOf(FiscalPolicy("1", 20.0), FiscalPolicy("2", 0.0))))

    val expected = Lists.newArrayList(
      CashRegister(id1, "any ip 1", "any ip 1", "any description", listOf(FiscalPolicy("1", 20.0))),
      CashRegister(id2, "any ip 2", "any ip 2", "any description", listOf(FiscalPolicy("1", 20.0), FiscalPolicy("2", 0.0)))
    )

    val actual = repository.getAll()

    assert(actual.equals(expected))
  }

  @Test (expected = DeviceAlreadyExistException::class)
  fun registerAlreadyPresentDevice() {
    repository.register(CashRegister("", "any ip", "any ip", "any description", listOf()))
    repository.register(CashRegister("", "any ip", "any ip", "any description", listOf()))
  }

  @Test
  fun findBySourceIp() {
    var expected = CashRegister("", "sourceIp", "any ip 1", "any description", listOf(FiscalPolicy(group="0", vat=20.0)))
    val id = repository.register(expected)
    expected = CashRegister(id, "sourceIp", "any ip 1", "any description", listOf(FiscalPolicy(group="0", vat=20.0)))
    val actual = repository.getBySourceIp("sourceIp").get()

    assert(actual.equals(expected))
  }

  @Test
  fun deleteCashRegister() {
    val toBeRegistered = CashRegister("", "sourceIp", "any ip 1", "any description", listOf())
    val id = repository.register(toBeRegistered)
    val registered = repository.getBySourceIp("sourceIp")
    repository.delete(id)
    val deleted = repository.getBySourceIp("sourceIp")

    assert(registered.isPresent)
    assert(!deleted.isPresent)
  }

  @Test (expected = DeviceDoesNotExistException::class)
  fun deleteNonExistingCashRegister() {
    repository.delete("58f9a3df0f43261f59c8b768")
  }
}