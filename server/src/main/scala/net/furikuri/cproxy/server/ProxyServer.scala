package net.furikuri.cproxy.server

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.io.Tcp._
import net.furikuri.cproxy.server.ProxyServer.ClientConnected

object ProxyServer {
  final case class ClientConnected(clientId: String) extends Event

  final case class ProxyRequest(clientId: String) extends Event
}

class ProxyServer(port: Int) extends Actor with ActorLogging {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("0.0.0.0", port))

  var clients:Map[String, ActorRef] = Map()

  def receive = {

    case ClientConnected(clientId) =>
      clients += clientId -> sender()
      log.info("Client added")

    case b @ Bound(localAddress) =>
      log.info("Bound")

    case CommandFailed(_: Bind) => context stop self

    case c @ Connected(remote, local) =>
      log.info("Connection received from hostname: " + remote.getHostName + " address: " + remote.getAddress.toString)
      val connection = sender()
      val handler = context.actorOf(Props(new PeerConnectionHandler(connection, clients)))
      connection ! Register(handler)
  }

}