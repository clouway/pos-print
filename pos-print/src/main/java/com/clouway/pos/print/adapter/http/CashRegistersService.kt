package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.core.CashRegister
import com.clouway.pos.print.core.ErrorResponse
import com.clouway.pos.print.persistent.DeviceAlreadyExistException
import com.clouway.pos.print.persistent.CashRegisterRepository
import com.google.common.collect.Lists
import com.google.inject.Inject
import com.google.sitebricks.At
import com.google.sitebricks.client.transport.Json
import com.google.sitebricks.headless.Reply
import com.google.sitebricks.headless.Request
import com.google.sitebricks.headless.Service
import com.google.sitebricks.http.Get
import com.google.sitebricks.http.Post
import javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST
import javax.servlet.http.HttpServletResponse.SC_CREATED

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
@Service
@At("/v1/devices")
class CashRegistersService @Inject constructor(private var repository: CashRegisterRepository) {

  @Get
  fun getDevices(): Reply<*> {
    val devicesDTO = adapt(repository.getAll())
    return Reply.with(devicesDTO).`as`(Json::class.java).ok()
  }

  @Post
  fun registerDevice(request: Request): Reply<*> {
    val cashRegisterDTO = request.read(CashRegisterDTO::class.java).`as`(Json::class.java)

    try {
      repository.register(CashRegister(cashRegisterDTO.sourceIp, cashRegisterDTO.destination, cashRegisterDTO.description))
    } catch (e: DeviceAlreadyExistException) {
      return Reply.with(ErrorResponse("Is already present")).status(SC_BAD_REQUEST)
    }

    return Reply.with(cashRegisterDTO.destination).status(SC_CREATED)
  }

  internal class CashRegisterDTO(var sourceIp: String = "", var destination: String = "", var description: String = "")

  internal fun adapt(devices: List<CashRegister>): List<CashRegisterDTO> {
    val devicesDTO = Lists.newArrayList<CashRegisterDTO>()

    devices.mapTo(devicesDTO) { CashRegisterDTO(it.sourceIp, it.destination, it.description) }

    return devicesDTO
  }
}
