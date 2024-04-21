package io.zola.zolabackend.api

import scala.concurrent.duration._
import scala.language.postfixOps


import akka.http.scaladsl.model._
import StatusCodes._
import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
import akka.http.scaladsl.server._
import Directives._

import org.scalatest.wordspec.AnyWordSpec

import spray.json._

import io.zola.zolabackend._

import marshalling._

class ApiServiceSpec extends AnyWordSpec
    with ScalatestRouteTest
    with ApiServiceT
{
  def actorRefFactory  = system

  val routeTestTimeout = RouteTestTimeout(FiniteDuration(10, "seconds"))

  Thread.sleep(3000)

  "ApiService" when {
    "given an empty POST request" should {
      "Reject and give a response" in {
        Post("/add/review", HttpEntity(ContentType(MediaTypes.`application/json`), """{}""".toJson.toString)) ~> Route.seal(route) ~> check {
        assert(status === BadRequest)
        val res = responseAs[String]
        assert(res.contains("""Object expected in field 'reviews'"""))
        }
      }
    }
    "given a post with an invalid parameter" should {
      "Reject and give a response" in {
        Post("/add/review", HttpEntity(ContentType(MediaTypes.`application/json`), """{}""".toJson.toString)) ~> Route.seal(route) ~> check {
        assert(status === BadRequest)
        val res = responseAs[String]
        assert(res.contains("""Object expected in field 'reviews'"""))
        }
      }
    }
  }
}