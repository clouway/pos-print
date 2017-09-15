package com.clouway.pos.print.core

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
data class CashRegister(val id: String = "", val sourceIp: String, val destination: String, val description: String, val fiscalPolicy: List<FiscalPolicy>)