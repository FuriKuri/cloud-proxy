package net.furikuri.cproxy.server

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.io.Tcp
import akka.util.ByteString

class TcpHandler(connection: ActorRef) extends Actor with ActorLogging {

  import Tcp._

  def receive = {
    case Received(data) =>
      val x = data.decodeString("utf-8")
      println("Received: " + x)
      connection ! Write(ByteString("HTTP/1.1 200 OK\n\nhello world"))
      connection ! Close

    case PeerClosed =>
      log.info("Peer closed")
      context.stop(self)
  }
}
