package com.epam.esc

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.epam.esc.bean.Info
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.{ExecutionContext, Future}

class ServerTest extends FunSuite with Routing with Matchers  with ScalatestRouteTest {

  val executionContext = ExecutionContext.Implicits.global

  val testInfo = Info(1, "test", "01-01-2000", "01-01-2000", "01-01-2000", "active", lastPhotos = Set.empty)
  val testUrl = "testUrl"

  override protected def getManifest(rover: String) = {
    rover match {
      case "test" =>
        Future.successful(testInfo)
      case _ =>
        Future.failed(new IllegalStateException("Unknown rover"))
    }
  }

  override protected def getPhoto(rover: String, sol: Int, camera: String) = {
    rover match {
      case "test" =>
        Future.successful(testUrl)
      case _ =>
        Future.failed(new IllegalStateException("Unknown rover"))
    }
  }

  test("Router should respond on GET request of /info") {
    Get("/info?rover=test") ~> routes ~> check {
      responseAs[Info] shouldEqual testInfo
    }
  }

  test("Router should respond error on GET request of /info for wrong rover") {
    Get("/info?rover=wrong") ~> routes ~> check {
      responseAs[String] shouldEqual "There was an internal server error."
    }
  }

  test("Router should respond on GET request of /photo") {
    Get("/photo?rover=test&sol=1&camera='fhaz'") ~> routes ~> check {
      responseAs[String] shouldEqual "<img src='testUrl'>"
    }
  }

  test("Router should respond error on GET request of /photo for wrong rover") {
    Get("/photo?rover=wrong&sol=1&camera='fhaz'") ~> routes ~> check {
      responseAs[String] shouldEqual "There was an internal server error."
    }
  }
}
