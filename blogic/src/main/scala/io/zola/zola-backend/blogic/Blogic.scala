package io.zola.zolabackend.core.blogic

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask

import io.zola.zolabackend._

import core.util.{ ZolaEnum, ZolaCCPrinter }
import ZolaEnum.ServiceStatus

import util._

object Blogic {

    case class AddReviewRequest(
      reviews: Seq[Review]
    ) extends ZolaCCPrinter
    case class AddBlockResponse(
      response: String,
      status: ServiceStatus.Value
    ) extends ZolaCCPrinter
    case class ListReviewRequest(
      index: Int
    ) extends ZolaCCPrinter
    case class ListReviewResponse(
      block: Option[List[Review]],
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
        append(req.reviews)
        currentSender ! AddBlockResponse(
          "Transactions appended successfully",
          ServiceStatus.Success
        )
      } catch {
        case ex: Throwable =>
          currentSender ! AddBlockResponse(
            "Error appending transactions",
            ServiceStatus.Failed
          )
      }
    case req: ListReviewRequest        =>
      log.info("processing " + req)
      val currentSender = sender()
      try {
        currentSender ! ListReviewResponse(
          Some(List(Review("Good Stuff")),
          ServiceStatus.Success
        ))
      } catch {
        case ex: Throwable =>
          currentSender ! ListReviewResponse(
            None,
            ServiceStatus.Failed
          )
      }
  }
}