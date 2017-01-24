package net.furikuri.cproxy.client

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}

import scala.util.control.NonFatal


object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Main")
    val app = system.actorOf(
      Props(new CloudProxyConnector(ProxyConfiguration.serverHost(), ProxyConfiguration.serverPort())))
    try {
      system.actorOf(Props(classOf[Terminator], app), "app-terminator")
    } catch {
      case NonFatal(e) => system.terminate(); throw e
    }
  }

  class Terminator(app: ActorRef) extends Actor with ActorLogging {
    context watch app

    def receive: Receive = {
      case Terminated(_) =>
        log.info("application supervisor has terminated, shutting down")
        context.system.terminate()
    }
  }
}
