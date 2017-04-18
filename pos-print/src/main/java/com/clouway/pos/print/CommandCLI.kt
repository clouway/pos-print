package com.clouway.pos.print

import com.sampullara.cli.Argument
import java.util.*

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class CommandCLI {

  @field:Argument(prefix = "--", description = "HTTP Listen Address")
  private var httpPort = 8080

  @field:Argument(prefix = "--", description = "Database hosts", delimiter = ";")
  private var dbHost = arrayOf("localhost:27017")

  @field:Argument(prefix = "--", description = "Database name")
  private var dbName = "pos_test"

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