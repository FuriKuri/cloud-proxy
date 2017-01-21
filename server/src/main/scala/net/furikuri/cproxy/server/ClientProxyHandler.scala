package net.furikuri.cproxy.server

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.io.Tcp
import akka.util.ByteString
import net.furikuri.cproxy.server.ProxyServer.ClientConnected

class ClientProxyHandler(connection: ActorRef, server: ActorRef) extends Actor with ActorLogging {

  import Tcp._

  var init = false
  var last: ActorRef = _

  var clientsActors:Map[UUID, ActorRef] = Map()

  def receive = {
    case Received(data) =>
      log.info("Receive new data")
      if (!init) {
        val clientId = data.decodeString("utf-8")
        println("Received: " + clientId)
        server ! ClientConnected(clientId)
        init = true
      } else {
        val value = data.decodeString("utf-8")
        val firstNewLine = value.indexOf("\n")
        val header = value.substring(0, firstNewLine)
        val rawData = data.drop(firstNewLine + 1).decodeString("utf-8")
        val uuid = header.split(":").apply(0)
        log.info("Got new header:\n" + header)
        log.info("Got new data:\n" + rawData)

        val uuidKey = UUID.fromString(uuid)
        val receiver = clientsActors(uuidKey)
        log.info("Return")

        receiver ! Write(data.drop(firstNewLine + 1))
      }

    case w: Write =>
      val uuid = UUID.randomUUID()
      log.info("Write new data")
      connection ! Write(ByteString(uuid.toString + ":" + w.data.length + "\n"))
      connection ! Write(w.data)
      clientsActors += uuid -> sender()
      last = sender()

    case PeerClosed =>
      log.info("Peer closed")
      context.stop(self)
  }
}
