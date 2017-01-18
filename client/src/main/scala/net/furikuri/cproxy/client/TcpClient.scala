package net.furikuri.cproxy.client

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString

object TcpClient {
  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[TcpClient], remote, replies)
}

class TcpClient(remote: InetSocketAddress, listener: ActorRef) extends Actor with ActorLogging {
  import Tcp._
  import context.system

  IO(Tcp) ! Connect(remote)

  def receive = {
    case CommandFailed(_: Connect) =>
      listener ! "connect failed"
      context stop self

    case c @ Connected(remote, local) =>
      listener ! c
      val connection = sender()
      connection ! Register(self)
      context become {
        case data: ByteString =>
          log.info("Write data")
          connection ! Write(data)
        case CommandFailed(w: Write) =>
          listener ! "write failed"
        case Received(data) =>
          log.info("New data")
          listener ! data
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          listener ! "connection closed"
          context stop self
      }
  }
}
