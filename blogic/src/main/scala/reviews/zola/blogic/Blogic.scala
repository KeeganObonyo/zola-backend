package reviews.zola.blogic

import scala.languageFeature.existentials

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.util.Timeout

import org.joda.time.DateTime

import reviews.zola._

import core.util.{ ZolaEnum, ZolaCCPrinter, ZolaConfig }
import ZolaEnum.ServiceStatus

import core.db.cassandra.service.UserReviewCassandraDbService
import core.db.cassandra.CassandraDbQueryResult

object Blogic {
    case class AddReviewRequest(
      userId: Int,
      rating: Int,
      businessName: String,
      callback: Option[String],
      textInfo: Option[String],
      authorName: String
    ) extends ZolaCCPrinter
    case class AddReviewResponse(
      description: String,
      status: ServiceStatus.Value
    ) extends ZolaCCPrinter
    case class ListReviewRequest(
      userId: Int
    ) extends ZolaCCPrinter
    case class Review(
      insertionTime: DateTime,
      rating: Int,
      callback: Option[String],
      textInfo: Option[String],
      authorName: String
    ) extends ZolaCCPrinter
    case class ListReviewResponse(
      reviews: Option[List[Review]],
      userId: Int,
      businessName: String,
      status: ServiceStatus.Value,
      rating: Float
    ) extends ZolaCCPrinter
}

class Blogic extends Actor with ActorLogging {

  implicit val system                          = context.system

  implicit val timeout                         = Timeout(ZolaConfig.serviceTimeout)

  private lazy val userReviewCassandraDbService = createUserReviewCassandraDbService
  def createUserReviewCassandraDbService        = context.actorOf(Props[UserReviewCassandraDbService])

  import Blogic._
  def receive: Receive = {
    case req: AddReviewRequest           =>
      log.info("processing " + req)
      val currentSender = sender()
      // try {
      //   currentSender ! AddReviewResponse(
      //     "Reviews appended successfully",
      //     ServiceStatus.Success
      //   )
      // } catch {
      //   case ex: Throwable =>
      //     currentSender ! AddReviewResponse(
      //       "Error appending transactions",
      //       ServiceStatus.Failed
      //     )
      // }
    case req: ListReviewRequest        =>
      log.info("processing " + req)
      val currentSender = sender()
      // try {
      //   currentSender ! ListReviewResponse(
      //     Some(List(Review("Good Stuff"))),
      //     ServiceStatus.Success
      //   )
      // } catch {
      //   case ex: Throwable =>
      //     currentSender ! ListReviewResponse(
      //       None,
      //       ServiceStatus.Failed
      //     )
      // }
  }
}