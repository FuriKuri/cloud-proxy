package net.furikuri.cproxy.client

import akka.actor.{Actor, ActorLogging}
import akka.io.Tcp.{CommandFailed, Connect, Connected, ConnectionClosed}


class CloudProxyConnector(host: String, port: Int) extends Actor with ActorLogging {
  override def receive: Receive = {
    case CommandFailed(conn: Connect) =>
      log.info(s"${sender}: CommandFaild ${conn}")
      context stop self

    case Connected(r, l) =>
      log.info(s"${sender}: The local ${l} connected to the remote ${r}")

    case cc: ConnectionClosed =>
      log.info(s"${sender}: ${cc}")
      context stop self
  }
}
