package com.clouway.pos.print.printer

import com.clouway.pos.print.core.CashRegister
import com.clouway.pos.print.core.DeviceNotFoundException
import com.clouway.pos.print.core.ReceiptPrinter
import com.clouway.pos.print.persistent.CashRegisterRepository
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.jmock.Expectations
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * @author Martin Milev <martin.milev@clouway.com>
 */
class FP705PrinterFactoryTest {
  @Rule @JvmField
  val context = JUnitRuleMockery()

  private val repository = context.mock(CashRegisterRepository::class.java)

  private val fakeFP705 = FakeFP705()
  private val factory = FP705PrinterFactory(repository)

  @Test
  fun happyPath() {
    val register = CashRegister("::ip::", "127.0.0.1:" + fakeFP705.port(), "::any description::")
    fakeFP705.startAsync().awaitRunning()

    context.checking(object : Expectations() {
      init {
        oneOf(repository).getBySourceIp("::ip::")
        will(returnValue(Optional.of(register)))
      }
    })

    assertThat(factory.getPrinter("::ip::"), instanceOf(ReceiptPrinter::class.java))
    fakeFP705.stopAsync().awaitTerminated()
  }

  @Test (expected = DeviceNotFoundException::class)
  fun deviceNotRegistered() {
    context.checking(object : Expectations() {
      init {
        oneOf(repository).getBySourceIp("::any id::")
        will(returnValue(Optional.empty<CashRegister>()))
      }
    })

    factory.getPrinter("::any id::")
  }
}