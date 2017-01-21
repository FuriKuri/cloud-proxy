package net.furikuri.cproxy.client

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.io.Tcp.{CommandFailed, Connect, Connected, ConnectionClosed}
import akka.util.ByteString
import net.furikuri.cproxy.client.CloudProxyConnector.LocalResponse


class LocalConnection(proxy: ActorRef, uuid: String, request: ByteString) extends Actor with ActorLogging {
  context.actorOf(TcpClient.props(new InetSocketAddress(ProxyConfiguration.domain(), 8080), self))

  override def receive: Receive = {
    case CommandFailed(conn: Connect) =>
      log.info(s"${sender}: CommandFaild ${conn}")
      context stop self

    case Connected(r, l) =>
      log.info(s"${sender}: The local ${l} connected to the remote ${r}")
      sender ! request

    case cc: ConnectionClosed =>
      log.info(s"${sender}: ${cc}")
      context stop self

    case data: ByteString =>
      log.info("Got response: " + data.decodeString("utf-8"))
      proxy ! LocalResponse(data, uuid)
  }
}
