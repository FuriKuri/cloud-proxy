package net.furikuri.cproxy.server

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.io.Tcp
import akka.util.ByteString


class PeerConnectionHandler(connection: ActorRef, clients: Map[String, ActorRef]) extends Actor with ActorLogging {

  import Tcp._

  def receive = {
    case Received(data) =>
      log.info("New request")
      val clientProxy = clients("hello")
      clientProxy ! Write(data)

    case w: Write =>
      log.info("Back to client")
      connection ! Write(w.data)

    case PeerClosed =>
      log.info("Peer closed")
      context.stop(self)
  }
}
