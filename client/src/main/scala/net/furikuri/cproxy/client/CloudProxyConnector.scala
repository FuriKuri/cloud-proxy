package net.furikuri.cproxy.client

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging}
import akka.io.Tcp.{CommandFailed, Connect, Connected, ConnectionClosed}
import akka.util.ByteString


class CloudProxyConnector(host: String, port: Int) extends Actor with ActorLogging {

  context.actorOf(TcpClient.props(new InetSocketAddress(host, port), self))

  override def receive: Receive = {
    case CommandFailed(conn: Connect) =>
      log.info(s"${sender}: CommandFaild ${conn}")
      context stop self

    case Connected(r, l) =>
      log.info(s"${sender}: The local ${l} connected to the remote ${r}")
      sender ! ByteString("Hello World")

    case cc: ConnectionClosed =>
      log.info(s"${sender}: ${cc}")
      context stop self
  }
}
