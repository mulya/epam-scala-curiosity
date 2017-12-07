package com.epam.esc.bean

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Info (
  maxSol: Int,
  name: String,
  landingDate: String,
  launchDate: String,
  maxDate: String,
  status: String,
  lastPhotos: Set[String]
)

trait InfoJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val infoSolFormat = jsonFormat7(Info)
}
