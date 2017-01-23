package net.furikuri.cproxy.client

import scala.util.Properties

object ProxyConfiguration {
  def targetDomain(): String = {
    Properties.envOrElse("CPROXY_TARGET_HOST", "localhost")
  }

  def targetPort(): Int = {
    Properties.envOrElse("CPROXY_TARGET_PORT", "8080").toInt
  }
}
