package io.zola.zola-backend.api

import scala.concurrent.duration._
import scala.language.postfixOps


import akka.http.scaladsl.model._
import StatusCodes._
import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
import akka.http.scaladsl.server._
import Directives._

import org.scalatest.{ Matchers, WordSpec }

import spray.json._

import io.zola.zola-backend._

import marshalling._

class ApiServiceSpec extends WordSpec
    with Matchers
    with ScalatestRouteTest
    with ApiServiceT
{
  def actorRefFactory  = system

  implicit val routeTestTimeout = RouteTestTimeout(FiniteDuration(10, "seconds"))

  Thread.sleep(3000)

  "ApiService" should {

    "Reject an empty POST request" in {
        Post("/add/review", HttpEntity(ContentType(MediaTypes.`application/json`), """{}""".toJson.toString)) ~> Route.seal(route) ~> check {
        status shouldEqual BadRequest
        val res = responseAs[String]
        assert(res.contains("""Object expected in field 'transactions'"""))
        }
    }
    "Reject POST request with an invalid parameter" in {
        Post("/add/review", HttpEntity(ContentType(MediaTypes.`application/json`), """{}""".toJson.toString)) ~> Route.seal(route) ~> check {
        status shouldEqual BadRequest
        val res = responseAs[String]
        assert(res.contains("""Object expected in field 'transactions'"""))
        }
    }
  }
}