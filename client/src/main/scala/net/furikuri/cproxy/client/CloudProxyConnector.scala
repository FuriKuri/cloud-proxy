package net.furikuri.cproxy.client

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging}
import akka.io.Tcp._
import akka.util.ByteString


class CloudProxyConnector(host: String, port: Int) extends Actor with ActorLogging {

  context.actorOf(TcpClient.props(new InetSocketAddress(host, port), self))

  override def receive: Receive = {
    case CommandFailed(conn: Connect) =>
      log.info(s"${sender}: CommandFaild ${conn}")
      context stop self

    case Connected(r, l) =>
      log.info(s"${sender}: The local ${l} connected to the remote ${r}")
      sender ! ByteString("hello")

    case cc: ConnectionClosed =>
      log.info(s"${sender}: ${cc}")
      context stop self

    case data: ByteString =>
      val value = data.decodeString("utf-8")
      val firstNewLine = value.indexOf("\n")
      val header = value.substring(0, firstNewLine)
      val rawData = value.substring(firstNewLine + 1)
      val uuid = header.split(":").apply(0)
      log.info("Got new header:\n" + header)
      log.info("Got new data:\n" + rawData)

      val response = ByteString("HTTP/1.1 200 OK\n\nHello World")
      sender ! ByteString(uuid + ":" + response.length + "\n")
      sender ! response
  }
}
