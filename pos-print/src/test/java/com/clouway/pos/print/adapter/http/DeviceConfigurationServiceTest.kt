package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.ReplyMatchers.Companion.contains
import com.clouway.pos.print.ReplyMatchers.Companion.isBadRequest
import com.clouway.pos.print.ReplyMatchers.Companion.isCreated
import com.clouway.pos.print.ReplyMatchers.Companion.isNoContent
import com.clouway.pos.print.ReplyMatchers.Companion.isOk
import com.clouway.pos.print.FakeRequest.Factory.newRequest
import com.clouway.pos.print.core.CashRegister
import com.clouway.pos.print.core.ErrorResponse
import com.clouway.pos.print.adapter.db.DeviceAlreadyExistException
import com.clouway.pos.print.adapter.db.CashRegisterRepository
import com.clouway.pos.print.adapter.db.DeviceDoesNotExistException
import com.clouway.pos.print.core.FiscalPolicy
import com.google.common.collect.Lists
import org.hamcrest.MatcherAssert.assertThat
import org.jmock.AbstractExpectations
import org.jmock.Expectations
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Rule
import org.junit.Test

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class DeviceConfigurationServiceTest {

  @Rule @JvmField
  var context = JUnitRuleMockery()

  private val repository = context.mock(CashRegisterRepository::class.java)

  private var cashRegistersService = DeviceConfigurationService(repository)

  @Test
  fun registerCashRegisterDevice() {
    val cashRegisterDTO = DeviceConfigurationService.CashRegisterDTO("", "89.100.10.5", "10.10.5.7", "cash register 1", listOf(FiscalPolicy("1", 20.0)))

    context.checking(object : Expectations() {
      init {
        oneOf(repository).register(CashRegister("", "89.100.10.5","10.10.5.7","cash register 1", listOf(FiscalPolicy("1", 20.0))))
        will(AbstractExpectations.returnValue("10.10.5.7"))
      }
    })


    val reply = cashRegistersService.registerDevice(newRequest(cashRegisterDTO))

    assertThat(reply, isCreated)
    assertThat(reply, contains("10.10.5.7"))
  }

  @Test
  fun deviceIsPresent() {
    val cashRegisterDTO = DeviceConfigurationService.CashRegisterDTO("", "89.100.10.5", "10.10.5.7", "cash register 1", listOf())

    context.checking(object : Expectations() {
      init {
        oneOf(repository).register(CashRegister("", "89.100.10.5","10.10.5.7","cash register 1", listOf()))
        will(AbstractExpectations.throwException(DeviceAlreadyExistException()))
      }
    })


    val reply = cashRegistersService.registerDevice(newRequest(cashRegisterDTO))

    assertThat(reply, isBadRequest)
    assertThat(reply, contains(ErrorResponse("Is already present")))
  }

  @Test
  fun retrieveRegisteredDevices() {
    context.checking(object : Expectations() {
      init {
        oneOf(repository).getAll()
        will(AbstractExpectations.returnValue(Lists.newArrayList<CashRegister>(
          CashRegister("", "89.100.10.5", "10.10.5.7", "cash register 1", listOf()),
          CashRegister("", "89.100.10.5", "10.10.5.8", "cash register 2", listOf()),
          CashRegister("", "89.100.10.5", "10.10.5.9", "cash register 3", listOf())
        )))
      }
    })

    val expected = Lists.newArrayList(
      DeviceConfigurationService.CashRegisterDTO("", "89.100.10.5", "10.10.5.7", "cash register 1", listOf() ),
      DeviceConfigurationService.CashRegisterDTO("", "89.100.10.5", "10.10.5.8", "cash register 2" , listOf()),
      DeviceConfigurationService.CashRegisterDTO("", "89.100.10.5", "10.10.5.9", "cash register 3", listOf())
    )

    val reply = cashRegistersService.getDevices()

    assertThat(reply, isOk)
    assertThat(reply, contains(expected))
  }

  @Test
  fun deleteRegisteredDevice() {
    context.checking(object : Expectations() {
      init {
        oneOf(repository).delete("::any id::")
        will(AbstractExpectations.returnValue("::any id::"))
      }
    })

    val reply = cashRegistersService.deleteDevice("::any id::")

    assertThat(reply, isNoContent)
    assertThat(reply, contains("::any id::"))
  }

  @Test
  fun noDeviceFoundToBeDeleted() {
    context.checking(object : Expectations() {
      init {
        oneOf(repository).delete("::any id::")
        will(AbstractExpectations.throwException(DeviceDoesNotExistException()))
      }
    })

    val reply = cashRegistersService.deleteDevice("::any id::")

    assertThat(reply, isBadRequest)
    assertThat(reply, contains(ErrorResponse("Not present")))
  }
}
