package com.clouway.pos.print

import com.google.gson.Gson
import com.google.sitebricks.headless.Reply
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.fail
import java.lang.reflect.Field
import javax.servlet.http.HttpServletResponse.*

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class ReplyMatchers {

  companion object {

    private val gson = Gson()

    fun containsValue(value: Any): Matcher<Reply<*>> {
      return contains(value)
    }

    fun contains(value: Any): Matcher<Reply<*>> {
      return object : TypeSafeMatcher<Reply<*>>() {
        public override fun matchesSafely(reply: Reply<*>): Boolean {
          val firstString = gson.toJson(value)
          val entity = property<Any>("entity", reply)
          val secondString = gson.toJson(entity)

          return firstString == secondString
        }

        override fun describeTo(description: Description) {
          description.appendText("reply value was different from expected one")
        }
      }
    }

    val isOk: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_OK)

    val isCreated: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_CREATED)

    val isAccepted: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_ACCEPTED)

    val isResetContent: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_RESET_CONTENT)

    val isBadRequest: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_BAD_REQUEST)

    val isNotFound: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_NOT_FOUND)

    val isInternalServerError: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_INTERNAL_SERVER_ERROR)

    val isNoContent: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_NO_CONTENT)

    val isUnavailable: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_SERVICE_UNAVAILABLE)

    val isForbidden: Matcher<Reply<*>>
      get() = returnCodeMatcher(SC_FORBIDDEN)

    fun isStatus(status: Int): Matcher<Reply<*>> {
      return returnCodeMatcher(status)
    }

    private fun returnCodeMatcher(expectedCode: Int): Matcher<Reply<*>> {
      return object : TypeSafeMatcher<Reply<*>>() {
        public override fun matchesSafely(reply: Reply<*>): Boolean {
          val status = property<Int>("status", reply)
          return Integer.valueOf(expectedCode) == status
        }

        override fun describeTo(description: Description) {
          description.appendText("status of the replay was different from expected")
        }
      }
    }


    private fun <T> property(fieldName: String, reply: Reply<*>): T? {
      var field: Field? = null
      try {
        field = reply.javaClass.getDeclaredField(fieldName)
        field!!.isAccessible = true
        val actual = field.get(reply) as T

        return actual
      } catch (e: NoSuchFieldException) {
        fail(e.message)
      } catch (e: IllegalAccessException) {
        fail(e.message)
      } finally {
        if (field != null) {
          field.isAccessible = false
        }
      }
      return null
    }
  }
}
