package com.epam.esc.bean

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class PhotoRequest(
  rover: String,
  sol: Int,
  camera: String
)

case class PhotoResponse(
  url: String
)

trait PhotoJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val photoRequestSolFormat = jsonFormat3(PhotoRequest)
  implicit val photoResponseSolFormat = jsonFormat1(PhotoResponse)
}
