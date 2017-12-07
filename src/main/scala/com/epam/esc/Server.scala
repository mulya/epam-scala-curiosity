package com.epam.esc

import akka.actor.ActorSystem
import akka.http.scaladsl.server.HttpApp
import akka.stream.ActorMaterializer
import com.epam.esc.bean._

import scala.concurrent.{ExecutionContextExecutor, Future}

object Server extends HttpApp with Routing {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val client = new Client()

  def main(args: Array[String]) = {
    Server.startServer("localhost", 8080)
  }

  override protected def getManifest(rover: String): Future[Info] = {
    client.getManifest(rover)
  }

  override protected def getPhoto(rover: String, sol: Int, camera: String): Future[String] = {
    client.getPhoto(rover, sol, camera)
  }
}