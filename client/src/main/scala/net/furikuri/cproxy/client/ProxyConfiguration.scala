package net.furikuri.cproxy.client

import scala.util.Properties

object ProxyConfiguration {
  def domain(): String = {
    Properties.envOrElse("CPROXY_TARGET_HOST", "localhost")
  }

  def port(): Int = {
    Properties.envOrElse("CPROXY_TARGET_PORT", "8080").toInt
  }
}
