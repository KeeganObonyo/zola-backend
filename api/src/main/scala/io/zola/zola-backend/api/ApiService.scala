package reviews.zola.api

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

import akka.actor.{ ActorRefFactory, Props }
import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout

import reviews.zola._

import core.util.ZolaConfig

import blogic.Blogic

import marshalling._

trait ApiServiceT extends WebJsonSupportT {

  def actorRefFactory: ActorRefFactory

  implicit val timeout = Timeout(ZolaConfig.webRequestTimeout)

  private val blogic = createBlogic
  def createBlogic   = actorRefFactory.actorOf(Props[Blogic]())

  import Blogic._
  lazy val route = {
    path("add" / "review") {
      logRequestResult("add:review", Logging.InfoLevel) {
        post {
          entity(as[AddReviewRequest]) { request =>
            complete(StatusCodes.Created, {
              (blogic ? request).mapTo[AddReviewResponse]
            })
          }
        }
      }
    } ~
    path("list" / "review") {
      logRequestResult("list:review", Logging.InfoLevel) {
        post {
          entity(as[ListReviewRequest]) { request =>
            complete(StatusCodes.OK, {
              (blogic ? request).mapTo[ListReviewResponse]
            })
          }
        }
      }
    }
  }
}
