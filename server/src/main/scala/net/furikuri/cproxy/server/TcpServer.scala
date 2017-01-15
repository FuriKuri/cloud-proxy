package net.furikuri.cproxy.server

import java.net.InetSocketAddress

import akka.io.{IO, Tcp}
import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.ActorLogging

class TcpServer extends Actor with ActorLogging {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("0.0.0.0", 4444))

  def receive = {
    case b @ Bound(localAddress) =>
      log.info("Bound")

    case CommandFailed(_: Bind) => context stop self

    case c @ Connected(remote, local) =>
      log.info("Connection received from hostname: " + remote.getHostName + " address: " + remote.getAddress.toString)
      val connection = sender()
      val handler = context.actorOf(Props(new TcpHandler(connection)))
      connection ! Register(handler)
  }

}