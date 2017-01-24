package net.furikuri.cproxy.client

import java.net.InetSocketAddress
import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp._
import akka.util.ByteString
import net.furikuri.cproxy.client.CloudProxyConnector.LocalResponse


object CloudProxyConnector {
  final case class LocalResponse(data: ByteString, uuid: String) extends Event
}

class CloudProxyConnector(host: String, port: Int) extends Actor with ActorLogging {

  var localConnections:Map[String, ActorRef] = Map()

  context.actorOf(TcpClient.props(new InetSocketAddress(host, port), self))

  override def receive: Receive = {
    case CommandFailed(conn: Connect) =>
      log.info(s"$sender: CommandFailed $conn")
      context stop self

    case Connected(r, l) =>
      log.info(s"$sender: The local $l connected to the remote $r")
      sender ! ByteString("hello")

    case cc: ConnectionClosed =>
      log.info(s"$sender: $cc")
      context stop self

    case data: ByteString =>
      val value = data.decodeString("utf-8")
      val firstNewLine = value.indexOf("\n")
      val header = value.substring(0, firstNewLine)
      val rawData = value.substring(firstNewLine + 1).replaceFirst("Host:[^\n]+\n", s"Host: ${ProxyConfiguration.targetHost()}\n")
      val uuid = header.split(":").apply(0)
      log.info("Got new header:\n" + header)
      log.info("Got new data:\n" + rawData)
      localConnections += uuid -> sender()
      context.actorOf(Props(classOf[LocalConnection], self, uuid, ByteString(rawData)))

    case LocalResponse(data, uuid) =>
      log.info("Forward bytes")
      val response = data
      val responseActor = localConnections(uuid)
      responseActor ! ByteString(uuid + ":" + response.length + "\n")
      responseActor ! response
  }
}
