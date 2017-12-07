package com.epam.esc

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer
import com.epam.esc.bean.{InfoJsonSupport, PhotoJsonSupport, PhotoRequest, PhotoResponse}

import scala.concurrent.ExecutionContextExecutor

object Server extends HttpApp with InfoJsonSupport with PhotoJsonSupport {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val client = new Client()

  override protected def routes: Route = path("info") {
    logRequest(("info-get", Logging.InfoLevel)) {
      get {
        parameter("rover") { rover =>
          complete(client.getManifest(rover))
        }
      }
    }
  } ~
    path("photo") {
      post {
        entity(as[PhotoRequest]) { photoRequest=>
          complete(client.getPhoto(photoRequest.rover, photoRequest.sol, photoRequest.camera).map{ url =>
            PhotoResponse(url)
          })
        }
      } ~
      get {
        parameter("rover", "sol".as[Int], "camera") { (rover, sol, camera) =>
          complete(client.getPhoto(rover, sol, camera).map{ url =>
            HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<img src='$url'>")
          })
        }
      }
    }

  def main(args: Array[String]) = {
    Server.startServer("localhost", 8080)
  }

}