package reviews.zola.api

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

import akka.actor.{ ActorRefFactory, Props }
import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
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
  lazy val route = cors(){
      {
      path("api" / "add" / "feedback") {
        logRequestResult("add:feedback", Logging.InfoLevel) {
          post {
            entity(as[AddReview]) { request =>
              // authenticateUser(request.username) { userId =>
                complete(StatusCodes.Created, {
                  (userReviewService ? request.getServiceRequest(1)).mapTo[AddReviewResponse]  map { x =>
                    AddResponse.fromServiceResponse(x)
                  }
                })
              // }
            }
          }
        }
      } ~
      path("api" / "list" / "review") {
        logRequestResult("list:review", Logging.InfoLevel) {
          post {
            entity(as[ListReview]) { request =>
              // authenticateUser(request.username) { userId =>
                complete(StatusCodes.OK, {
                  (userReviewService ? request.getServiceRequest(2)).mapTo[ListReviewResponse]
                })
              // }
            }
          }
        }
      } ~
      path("api" / "list" / "feedback") {
        logRequestResult("list:feedback", Logging.InfoLevel) {
          post {
            entity(as[ListFeedBack]) { request =>
              // authenticateUser(request.username) { userId =>
                complete(StatusCodes.OK, {
                  (userReviewService ? request.getServiceRequest(2)).mapTo[ListFeedBackResponse]
                })
              // }
            }
          }
        }
      } ~
      path("api" / "place") {
        logRequestResult("place:id", Logging.InfoLevel) {
          post {
            entity(as[PlaceIdRequest]) { request =>
              // authenticateUser(request.username) { userId =>
                complete(StatusCodes.OK, {
                  (userReviewService ? request).mapTo[PlaceIdResponse]
                })
              // }
            }
          }
        }
      }
    }
  }
}
