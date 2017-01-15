package net.furikuri.cproxy.server

import akka.actor.{ActorSystem, Props}

object ServerTest extends App {
  val system = ActorSystem("TcpServerTest")
  val server = system.actorOf(Props[TcpServer])
}
