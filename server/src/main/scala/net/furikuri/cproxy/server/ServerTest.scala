package net.furikuri.cproxy.server

import akka.actor.{ActorSystem, Props}

object ServerTest extends App {
  val system = ActorSystem("TcpServerTest")
  val endpointServer = system.actorOf(Props(new ProxyServer(8080)))
  val clientProxyServer = system.actorOf(Props(new ClientProxy(4444, endpointServer)))
}
