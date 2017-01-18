package net.furikuri.cproxy.server

import java.net.InetSocketAddress

import akka.io.{IO, Tcp}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

class ClientProxy(port: Int, server: ActorRef) extends Actor with ActorLogging {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("0.0.0.0", port))

  def receive = {
    case b @ Bound(localAddress) =>
      log.info("Bound")

    case CommandFailed(_: Bind) => context stop self

    case c @ Connected(remote, local) =>
      log.info("Connection received from hostname: " + remote.getHostName + " address: " + remote.getAddress.toString)
      val connection = sender()
      val handler = context.actorOf(Props(new ClientProxyHandler(connection, server)))
      connection ! Register(handler)
  }

}