package com.clouway.pos.print.core

import com.sampullara.cli.Argument
import java.util.*

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class CommandCLI {

  @Argument(prefix = "--", description = "HTTP Listen Address")
  private val httpPort = 8080

  @Argument(prefix = "--", description = "Database hosts", delimiter = ";")
  private val dbHost = arrayOf("localhost:27017")

  @Argument(prefix = "--", description = "Database name")
  private val dbName = "pos_test"

  fun httpPort(): Int {
    return httpPort
  }

  fun dbName(): String {
    return dbName
  }

  fun dbHost(): List<String> {
    return Arrays.asList(*dbHost)
  }
}