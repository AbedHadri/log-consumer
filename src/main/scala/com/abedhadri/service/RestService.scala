package com.abedhadri.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class RestService extends SprayJsonSupport {

  val routes: Route = get {
    path("status") {
      complete("Hello World")
    }
  }

}
