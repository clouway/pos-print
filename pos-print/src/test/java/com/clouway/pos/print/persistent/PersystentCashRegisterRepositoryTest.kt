package com.clouway.pos.print.persistent

import com.clouway.pos.print.core.CashRegister
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
    val expected = Lists.newArrayList(
      CashRegister("any ip 1", "any ip 1", "any description"),
      CashRegister("any ip 2", "any ip 2", "any description")
    )

    repository.register(CashRegister("any ip 1", "any ip 1", "any description"))
    repository.register(CashRegister("any ip 2", "any ip 2", "any description"))

    val actual = repository.getAll()

    assert(actual.equals(expected))
  }

  @Test (expected = DeviceAlreadyExistException::class)
  fun registerAlreadyPresentDevice() {
    repository.register(CashRegister("any ip", "any ip", "any description"))
    repository.register(CashRegister("any ip", "any ip", "any description"))
  }
}