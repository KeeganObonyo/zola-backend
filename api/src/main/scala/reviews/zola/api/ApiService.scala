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

import core.util.{ ZolaConfig, ZolaCoreServiceT }

import blogic.UserReviewService

import marshalling._

trait ApiServiceT extends WebJsonSupportT
  with ZolaCoreServiceT
  with UserAuthenticationDirectiveT {

  def actorRefFactory: ActorRefFactory

  implicit val timeout = Timeout(ZolaConfig.webRequestTimeout)

  private val userReviewService = createUserReviewService
  def createUserReviewService   = actorRefFactory.actorOf(Props[UserReviewService]())

  import UserReviewService._
  lazy val route = {
    path("add" / "review") {
      logRequestResult("add:review", Logging.InfoLevel) {
        post {
          entity(as[AddReview]) { request =>
            authenticateUser(request.username) { userId =>
              complete(StatusCodes.Created, {
                (userReviewService ? request.getServiceRequest(userId)).mapTo[AddReviewResponse]  map { x =>
                  AddResponse.fromServiceResponse(x)
                }
              })
            }
          }
        }
      }
    } ~
    path("list" / "review") {
      logRequestResult("list:review", Logging.InfoLevel) {
        post {
          entity(as[ListReview]) { request =>
            authenticateUser(request.username) { userId =>
              complete(StatusCodes.OK, {
                (userReviewService ? request.getServiceRequest(userId)).mapTo[ListReviewResponse]
              })
            }
          }
        }
      }
    }
  }
}
