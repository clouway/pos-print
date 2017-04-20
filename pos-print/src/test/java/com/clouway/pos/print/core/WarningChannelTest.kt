package com.clouway.pos.print.core

import com.clouway.pos.print.printer.Response
import com.clouway.pos.print.printer.Status
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class WarningChannelTest {
  val anyRequest = ByteArray(0)

  @Test
  fun noWarnings() {
    val noWarningStatus = byteArrayOf(
      0x80.toByte(), 0x80.toByte(), 0x80.toByte(),
      0x80.toByte(), 0x86.toByte(), 0x9A.toByte(),
      0x80.toByte(), 0x80.toByte()
    )

    val warningChan = WarningChannel(channelSendWillReturnStatus(noWarningStatus))
    warningChan.sendPacket(anyRequest)
    assertThat(warningChan.warnings(), equalTo(setOf<Status>()))
  }

  @Test
  fun oneWarning() {
    val oneWarning = byteArrayOf(
      0x80.toByte(), 0x80.toByte(), 0x82.toByte(), 0x80.toByte(),
      0x86.toByte(), 0x9A.toByte(), 0x80.toByte(), 0x80.toByte()
    )

    val warningChan = WarningChannel(channelSendWillReturnStatus(oneWarning))
    warningChan.sendPacket(anyRequest)

    assertThat(warningChan.warnings(), equalTo(setOf(Status.NEAR_PAPER_END)))
  }

  @Test
  fun fewWarnings() {
    val oneWarning = byteArrayOf(
      0x80.toByte(), 0x80.toByte(), 0x83.toByte(), 0x80.toByte(),
      0x86.toByte(), 0x9A.toByte(), 0x80.toByte(), 0x80.toByte()
    )

    val warningChan = WarningChannel(channelSendWillReturnStatus(oneWarning))
    warningChan.sendPacket(anyRequest)

    assertThat(warningChan.warnings(), equalTo(setOf(Status.NEAR_PAPER_END, Status.END_OF_PAPER)))
  }

  fun channelSendWillReturnStatus(statusBytes: ByteArray): Channel {
    return object : Channel {
      override fun sendPacket(request: ByteArray): Response {
        return object : Response {

          override fun isSuccessful(): Boolean {
            return true
          }

          override fun data(): ByteArray {
            return byteArrayOf()
          }

          override fun raw(): ByteArray {
            return byteArrayOf()
          }

          override fun status(): ByteArray {
            return statusBytes
          }

        }
      }

    }
  }
}