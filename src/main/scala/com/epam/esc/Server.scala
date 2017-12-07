package com.epam.esc

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

object Server extends HttpApp {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val client = new Client()

  override protected def routes: Route = pathSingleSlash {
    get {
      val urlFuture = client.getManifest().flatMap{ manifest =>
        client.getLastPhoto(manifest)
      }
      complete(urlFuture)
    }
  }

  def main(args: Array[String]) = {
    Server.startServer("localhost", 8080)
  }

}