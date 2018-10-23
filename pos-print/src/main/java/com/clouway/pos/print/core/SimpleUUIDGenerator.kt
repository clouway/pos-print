package com.clouway.pos.print.core

import java.util.UUID

/**
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
class SimpleUUIDGenerator : IdGenerator {
  override fun newId(): String {
    return UUID.randomUUID().toString()
  }
}