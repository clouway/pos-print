package com.clouway.pos.print.core

/**
 * Provides the methods to generate unique ids.
 *
 * @author Tsvetozar Bonev (tsvetozar.bonev@clouway.com)
 */
interface IdGenerator {
  fun newId() : String
}