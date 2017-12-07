package com.epam.esc

import akka.http.scaladsl.server.{HttpApp, Route}

object Server extends HttpApp {

    override protected def routes: Route = pathSingleSlash {
    get {
      complete("Hello from Server")
    }
  }

  def main(args: Array[String]) = {
    Server.startServer("localhost", 8080)
  }

}