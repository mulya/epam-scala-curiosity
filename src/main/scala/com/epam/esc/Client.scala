package com.epam.esc

import java.nio.charset.Charset

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.epam.esc.bean.Info
import com.epam.esc.bean.nasa._
import spray.json._

import scala.concurrent.{ExecutionContextExecutor, Future}

class Client()(
  implicit val system: ActorSystem,
  implicit val materializer: ActorMaterializer,
  implicit val executionContext: ExecutionContextExecutor
) extends ManifestJsonSupport {

  val nasaApiUrl = "https://api.nasa.gov/mars-photos/api/v1"
  val key = "6b61IprIJxLY1GkdDcKKIq3BMb6DQtBH1krnhxe3"

  def getManifest(rover: String): Future[Info] = {
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(uri = s"$nasaApiUrl/manifests/$rover?api_key=$key"))

    responseFuture.flatMap { res: HttpResponse =>
      res.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
        val manifest = body.decodeString(Charset.defaultCharset()).parseJson.convertTo[Manifest]
        Info(
          manifest.photo_manifest.max_sol,
          manifest.photo_manifest.name,
          manifest.photo_manifest.landing_date,
          manifest.photo_manifest.launch_date,
          manifest.photo_manifest.max_date,
          manifest.photo_manifest.status,
          manifest.photo_manifest.photos.maxBy(_.sol).cameras
        )
      }
    }
  }

  def getPhoto(rover: String, sol: Int, camera: String): Future[String] = {
    val uri = s"$nasaApiUrl/rovers/$rover/photos?sol=$sol&camera=$camera&api_key=$key"
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