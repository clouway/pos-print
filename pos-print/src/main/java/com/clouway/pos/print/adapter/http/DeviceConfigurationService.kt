package com.clouway.pos.print.adapter.http

import com.clouway.pos.print.adapter.db.CashRegisterRepository
import com.clouway.pos.print.adapter.db.DeviceAlreadyExistException
import com.clouway.pos.print.adapter.db.DeviceDoesNotExistException
import com.clouway.pos.print.core.CashRegister
import com.clouway.pos.print.core.ErrorResponse
import com.clouway.pos.print.core.FiscalPolicy
import com.clouway.pos.print.transport.GsonTransport
import com.google.common.collect.Lists
import com.google.inject.Inject
import com.google.inject.name.Named
import com.google.sitebricks.At
import com.google.sitebricks.client.transport.Json
import com.google.sitebricks.headless.Reply
import com.google.sitebricks.headless.Request
import com.google.sitebricks.headless.Service
import com.google.sitebricks.http.Delete
import com.google.sitebricks.http.Get
import com.google.sitebricks.http.Post
import javax.servlet.http.HttpServletResponse.*

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
@Service
@At("/v1/devices")
class DeviceConfigurationService @Inject constructor(private var repository: CashRegisterRepository) {

  @Get
  fun getDevices(): Reply<*> {
    val devicesDTO = adapt(repository.getAll())
    return Reply.with(devicesDTO).`as`(GsonTransport::class.java).ok()
  }

  @Post
  fun registerDevice(request: Request): Reply<*> {
    val cashRegisterDTO = request.read(CashRegisterDTO::class.java).`as`(GsonTransport::class.java)
    val deviceId: String

    try {
      deviceId = repository.register(CashRegister(cashRegisterDTO.id, cashRegisterDTO.sourceIp, cashRegisterDTO.destination, cashRegisterDTO.description, cashRegisterDTO.fiscalPolicy))
    } catch (e: DeviceAlreadyExistException) {
      return Reply.with(ErrorResponse("Is already present")).`as`(GsonTransport::class.java).status(SC_BAD_REQUEST)
    }

    return Reply.with(deviceId).`as`(GsonTransport::class.java).status(SC_CREATED)
  }

  @Delete
  @At("/:id")
  fun deleteDevice(@Named("id") id: String): Reply<*> {
    val deviceId: String

    try {
      deviceId = repository.delete(id)
    } catch (e: DeviceDoesNotExistException) {
      return Reply.with(ErrorResponse("Not present")).`as`(GsonTransport::class.java).status(SC_BAD_REQUEST)
    }

    return Reply.with(deviceId).`as`(GsonTransport::class.java).status(SC_NO_CONTENT)
  }

  internal class CashRegisterDTO(val id: String = "", val sourceIp: String = "", val destination: String = "", val description: String = "", val fiscalPolicy: List<FiscalPolicy>)

  internal fun adapt(devices: List<CashRegister>): List<CashRegisterDTO> {
    val devicesDTO = Lists.newArrayList<CashRegisterDTO>()
    devices.mapTo(devicesDTO) { CashRegisterDTO(it.id, it.sourceIp, it.destination, it.description, it.fiscalPolicy) }
    return devicesDTO
  }
}
