package com.epam.esc.bean

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Manifest (
  photo_manifest: PhotoManifest
)

case class PhotoManifest (
  max_sol: Int,
  name: String,
  landing_date: String,
  launch_date: String,
  max_date: String,
  status: String,
  total_photos: Int,
  photos: List[PhotoSol]
)

case class PhotoSol (
  sol: Int,
  total_photos: Int,
  cameras: Set[String]
)

trait ManifestJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val photoSolFormat = jsonFormat3(PhotoSol)
  implicit val photoManifestFormat = jsonFormat8(PhotoManifest)
  implicit val manifestFormat = jsonFormat1(Manifest)
}