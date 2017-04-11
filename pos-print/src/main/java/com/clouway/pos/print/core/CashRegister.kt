package com.clouway.pos.print.core

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class CashRegister(val sourceIp: String, val destination: String, val description: String) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other?.javaClass != javaClass) return false

    other as CashRegister

    if (sourceIp != other.sourceIp) return false
    if (destination != other.destination) return false
    if (description != other.description) return false

    return true
  }

  override fun hashCode(): Int {
    var result = sourceIp.hashCode()
    result = 31 * result + destination.hashCode()
    result = 31 * result + description.hashCode()
    return result
  }
}