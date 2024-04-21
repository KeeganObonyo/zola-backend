package reviews.zola.blogic

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask

import reviews.zola._

import core.util.{ ZolaEnum, ZolaCCPrinter }
import ZolaEnum.ServiceStatus

import util._
import scala.languageFeature.existentials

object Blogic {
    case class Review(
      text: String
    ) extends ZolaCCPrinter
    case class AddReviewRequest(
      reviews: Review
    ) extends ZolaCCPrinter
    case class AddReviewResponse(
      response: String,
      status: ServiceStatus.Value
    ) extends ZolaCCPrinter
    case class ListReviewRequest(
      index: Int
    ) extends ZolaCCPrinter
    case class ListReviewResponse(
      reviews: Option[List[Review]],
      status: ServiceStatus.Value
    ) extends ZolaCCPrinter
}

class Blogic extends Actor with ActorLogging {

  import Blogic._
  def receive: Receive = {
    case req: AddReviewRequest           =>
      log.info("processing " + req)
      val currentSender = sender()
      try {
        currentSender ! AddReviewResponse(
          "Reviews appended successfully",
          ServiceStatus.Success
        )
      } catch {
        case ex: Throwable =>
          currentSender ! AddReviewResponse(
            "Error appending transactions",
            ServiceStatus.Failed
          )
      }
    case req: ListReviewRequest        =>
      log.info("processing " + req)
      val currentSender = sender()
      try {
        currentSender ! ListReviewResponse(
          Some(List(Review("Good Stuff"))),
          ServiceStatus.Success
        )
      } catch {
        case ex: Throwable =>
          currentSender ! ListReviewResponse(
            None,
            ServiceStatus.Failed
          )
      }
  }
}