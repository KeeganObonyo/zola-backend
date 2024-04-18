package io.zola.zola-backend.api

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

import akka.actor.{ ActorRefFactory, Props }
import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout

import io.zola.zola-backend._

import core.util.ZolaConfig

import blogic.Blogic

import marshalling._

trait ApiServiceT extends WebJsonSupportT {

  def actorRefFactory: ActorRefFactory

  implicit val timeout       = Timeout(ZolaBackendConfig.webRequestTimeout)

  private val fastBlockchain = createFastBlockchain
  def createFastBlockchain   = actorRefFactory.actorOf(Props[Blogic]())

  import Blogic._
  lazy val route = {
    path("add" / "review") {
      logRequestResult("add:block", Logging.InfoLevel) {
        post {
          entity(as[AddReviewRequest]) { request =>
            complete(StatusCodes.Created, {
              (fastBlockchain ? request).mapTo[AddReviewResponse]
            })
          }
        }
      }
    } ~
    path("list" / "review") {
      logRequestResult("find-by:index", Logging.InfoLevel) {
        post {
          entity(as[ListReviewRequest]) { request =>
            complete(StatusCodes.OK, {
              (fastBlockchain ? request).mapTo[ListReviewResponse]
            })
          }
        }
      }
    }
  }
}
