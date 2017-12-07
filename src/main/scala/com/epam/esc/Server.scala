package com.epam.esc

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer
import com.epam.esc.bean.InfoJsonSupport

import scala.concurrent.ExecutionContextExecutor

object Server extends HttpApp with InfoJsonSupport {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val client = new Client()

  override protected def routes: Route = path("info") {
    get {
      parameter("rover") { rover =>
        complete(client.getManifest(rover))
      }
    }
  }

  def main(args: Array[String]) = {
    Server.startServer("localhost", 8080)
  }

}