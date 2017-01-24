package net.furikuri.cproxy.client

import scala.util.Properties

object ProxyConfiguration {
  def targetHost(): String = {
    Properties.envOrElse("CPROXY_TARGET_HOST", "localhost")
  }

  def targetPort(): Int = {
    Properties.envOrElse("CPROXY_TARGET_PORT", "8080").toInt
  }

  def serverHost(): String = {
    Properties.envOrElse("CPROXY_SERVER_HOST", "localhost")
  }

  def serverPort(): Int = {
    Properties.envOrElse("CPROXY_SERVER_PORT", "4444").toInt
  }
}
