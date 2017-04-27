package com.clouway.pos.print

import com.google.common.collect.Multimap
import com.google.inject.TypeLiteral
import com.google.sitebricks.client.Transport
import com.google.sitebricks.headless.Request
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.OutputStream

/**
 *@author Borislav Gadjev <borislav.gadjev@clouway.com>
 */
class FakeRequest {

  companion object Factory {
    fun <E> newRequest(request: E): Request {
      return object : Request {
        private val e = request

        override fun <E> read(type: Class<E>): Request.RequestRead<E> {
          return Request.RequestRead { request as E }
        }

        override fun <E> read(type: TypeLiteral<E>): Request.RequestRead<E>? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Throws(IOException::class)
        override fun readTo(out: OutputStream) {
          if (e is String) {
            out.write(e.toByteArray())
          } else {
            throw IllegalStateException("Try to test with some string ! ")
          }
        }

        override fun headers(): Multimap<String, String>? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun params(): Multimap<String, String>? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun matrix(): Multimap<String, String>? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun matrixParam(name: String): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun param(name: String): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun header(name: String): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun uri(): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun path(): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun context(): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun method(): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun validate(o: Any) {

        }
      }
    }

    fun <E> newJsonRequest(request: E): Request {
      return object : Request {
        private val e = request

        override fun <E> read(type: Class<E>): Request.RequestRead<E> {
          return Request.RequestRead<E> { p0 ->
            val transport = p0.newInstance()
            transport.`in`(ByteArrayInputStream(e.toString().toByteArray()), type)
          }
        }

        override fun <E> read(type: TypeLiteral<E>): Request.RequestRead<E>? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Throws(IOException::class)
        override fun readTo(out: OutputStream) {
          if (e is String) {
            out.write(e.toByteArray())
          } else {
            throw IllegalStateException("Try to test with some string ! ")
          }
        }

        override fun headers(): Multimap<String, String>? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun params(): Multimap<String, String>? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun matrix(): Multimap<String, String>? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun matrixParam(name: String): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun param(name: String): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun header(name: String): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun uri(): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun path(): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun context(): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun method(): String? {
          return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        override fun validate(o: Any) {

        }
      }
    }
  }

}