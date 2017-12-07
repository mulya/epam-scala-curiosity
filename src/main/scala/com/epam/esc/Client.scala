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

class Client()(
  implicit val system: ActorSystem,
  implicit val materializer: ActorMaterializer,
  implicit val executionContext: ExecutionContextExecutor
) extends ManifestJsonSupport {

  val nasaApiUrl = "https://api.nasa.gov/mars-photos/api/v1"
  val key = "6b61IprIJxLY1GkdDcKKIq3BMb6DQtBH1krnhxe3"
  val rover = "curiosity"

  def main(args: Array[String]): Unit = {

    getManifest().flatMap{ manifest =>
      getLastPhoto(manifest)
    }.onComplete{
      case Success(url: String) =>
        println(url)
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

  def getLastPhoto(manifest: Manifest): Future[String] = {
    val photoSol = manifest.photo_manifest.photos.maxBy(_.sol)
    val uri = s"$nasaApiUrl/rovers/$rover/photos?sol=${photoSol.sol}&camera=${photoSol.cameras.head}&api_key=$key"
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(uri = uri))

    responseFuture.flatMap { res: HttpResponse =>
      res.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
        body.decodeString(Charset.defaultCharset()).parseJson
          .asJsObject.fields("photos")
          .asInstanceOf[JsArray].elements.head
          .asJsObject.fields("img_src")
          .asInstanceOf[JsString]
          .value
      }
    }
  }
}