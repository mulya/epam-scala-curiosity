package com.epam.esc

import akka.event.Logging
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import com.epam.esc.bean._

import scala.concurrent.{ExecutionContext, Future}

trait Routing extends Directives with InfoJsonSupport with PhotoJsonSupport {

  protected def routes: Route = path("info") {
    logRequest(("info-get", Logging.InfoLevel)) {
      get {
        parameter("rover") { rover =>
          complete(getManifest(rover))
        }
      }
    }
  } ~
    path("photo") {
      post {
        entity(as[PhotoRequest]) { photoRequest=>
          complete(getPhoto(photoRequest.rover, photoRequest.sol, photoRequest.camera).map{ url =>
            PhotoResponse(url)
          })
        }
      } ~
        get {
          parameter("rover", "sol".as[Int], "camera") { (rover, sol, camera) =>
            complete(getPhoto(rover, sol, camera).map{ url =>
              HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<img src='$url'>")
            })
          }
        }
    }

  implicit def executionContext: ExecutionContext

  protected def getManifest(rover: String): Future[Info]

  protected def getPhoto(rover: String, sol: Int, camera: String): Future[String]
}
