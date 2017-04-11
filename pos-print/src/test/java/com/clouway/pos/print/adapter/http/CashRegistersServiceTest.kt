package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.ReplyMatchers.Companion.contains
import com.clouway.pos.print.ReplyMatchers.Companion.isBadRequest
import com.clouway.pos.print.ReplyMatchers.Companion.isCreated
import com.clouway.pos.print.ReplyMatchers.Companion.isOk
import com.clouway.pos.print.SiteBricksRequestMockery
import com.clouway.pos.print.core.CashRegister
import com.clouway.pos.print.core.ErrorResponse
import com.clouway.pos.print.persistent.DeviceAlreadyExistException
import com.clouway.pos.print.persistent.CashRegisterRepository
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
class CashRegistersServiceTest {

  @Rule @JvmField
  var context = JUnitRuleMockery()

  private val repository = context.mock(CashRegisterRepository::class.java)

  private val mockRequest = SiteBricksRequestMockery()
  private var cashRegistersService = CashRegistersService(repository)

  @Test
  fun registerCashRegisterDevice() {
    val cashRegisterDTO = CashRegistersService.CashRegisterDTO("89.100.10.5", "10.10.5.7", "cash register 1")

    context.checking(object : Expectations() {
      init {
        oneOf(repository).register(CashRegister("89.100.10.5","10.10.5.7","cash register 1"))
        will(AbstractExpectations.returnValue("10.10.5.7"))
      }
    })


    val reply = cashRegistersService.registerDevice(mockRequest.mockRequest(cashRegisterDTO))

    assertThat(reply, isCreated)
    assertThat(reply, contains("10.10.5.7"))
  }

  @Test
  fun deviceIsPresent() {
    val cashRegisterDTO = CashRegistersService.CashRegisterDTO("89.100.10.5", "10.10.5.7", "cash register 1")

    context.checking(object : Expectations() {
      init {
        oneOf(repository).register(CashRegister("89.100.10.5","10.10.5.7","cash register 1"))
        will(AbstractExpectations.throwException(DeviceAlreadyExistException()))
      }
    })


    val reply = cashRegistersService.registerDevice(mockRequest.mockRequest(cashRegisterDTO))

    assertThat(reply, isBadRequest)
    assertThat(reply, contains(ErrorResponse("Is already present")))
  }

  @Test
  fun retrieveRegisteredDevices() {
    context.checking(object : Expectations() {
      init {
        oneOf(repository).getAll()
        will(AbstractExpectations.returnValue(Lists.newArrayList<CashRegister>(
          CashRegister("89.100.10.5", "10.10.5.7", "cash register 1"),
          CashRegister("89.100.10.5", "10.10.5.8", "cash register 2"),
          CashRegister("89.100.10.5", "10.10.5.9", "cash register 3")
        )))
      }
    })

    val expected = Lists.newArrayList(
      CashRegistersService.CashRegisterDTO("89.100.10.5", "10.10.5.7", "cash register 1"),
      CashRegistersService.CashRegisterDTO("89.100.10.5", "10.10.5.8", "cash register 2"),
      CashRegistersService.CashRegisterDTO("89.100.10.5", "10.10.5.9", "cash register 3")
    )

    val reply = cashRegistersService.getDevices()

    assertThat(reply, isOk)
    assertThat(reply, contains(expected))
  }
}
