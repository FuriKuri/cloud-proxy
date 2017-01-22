package net.furikuri.cproxy.client

import scala.util.Properties

object ProxyConfiguration {
  def domain(): String = {
    Properties.envOrElse("CPROXY_HOST", "localhost")
  }

  def port(): Int = {
    Properties.envOrElse("CPROXY_PORT", "8080").toInt
  }
}
