package net.furikuri.cproxy.server

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.io.Tcp
import akka.util.ByteString
import net.furikuri.cproxy.server.ProxyServer.ClientConnected

class ClientProxyHandler(connection: ActorRef, server: ActorRef) extends Actor with ActorLogging {

  import Tcp._

  var init = false
  var last: ActorRef = _

  def receive = {
    case Received(data) =>
      log.info("Receive new data")
      if (!init) {
        val clientId = data.decodeString("utf-8")
        println("Received: " + clientId)
        server ! ClientConnected(clientId)
//        connection ! Write(ByteString("OK"))
        init = true
      } else {
        log.info("Return")
        last ! Write(data)
      }

    case w: Write =>
      log.info("Write new data")
      connection ! Write(w.data)
      last = sender()

    case PeerClosed =>
      log.info("Peer closed")
      context.stop(self)
  }
}
