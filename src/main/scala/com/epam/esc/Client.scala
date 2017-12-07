package com.epam.esc

import java.nio.charset.Charset

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import spray.json._
import com.epam.esc.bean.{Manifest, ManifestJsonSupport}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Client extends ManifestJsonSupport {

  val nasaApiUrl = "https://api.nasa.gov/mars-photos/api/v1"
  val key = "6b61IprIJxLY1GkdDcKKIq3BMb6DQtBH1krnhxe3"
  val rover = "curiosity"

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def main(args: Array[String]): Unit = {

    getManifest().onComplete{
      case Success(m: Manifest) =>
        println(m.photo_manifest.photos.maxBy(_.sol))
      case Failure(e) =>
        println("Failure: " + e)
    }

  }

  def getManifest(): Future[Manifest] = {
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(uri = s"$nasaApiUrl/manifests/$rover?api_key=$key"))

    responseFuture.flatMap { res: HttpResponse =>
      res.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
        body.decodeString(Charset.defaultCharset()).parseJson.convertTo[Manifest]
      }
    }
  }
}