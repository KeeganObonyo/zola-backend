package reviews.zola.core
package db.cassandra.service

import java.net.URL

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{ Actor, ActorLogging }
import akka.pattern.pipe

import org.joda.time.DateTime

import com.outworkers.phantom.dsl._

import reviews.zola._

import core.util.ZolaCCPrinter

import core.db.cassandra.CassandraDbQueryResult
import core.db.cassandra.ZolaCassandraDb

import core.db.cassandra.mapper.UserReviewMapper

object UserReviewCassandraDbService {
  case class UserReviewDbEntry(
    userId: Int,
    transactionId: String,
    insertionTime: DateTime,
    rating: Int,
    businessName: String,
    callback: Option[String],
    textInfo: String,
    authorName: String
  )extends ZolaCCPrinter

  case class UserReviewCreateDbQuery(
    userId: Int,
    transactionId: String,
    insertionTime: DateTime,
    rating: Int,
    businessName: String,
    callback: Option[String],
    textInfo: String,
    authorName: String
  )extends ZolaCCPrinter
  case class UserReviewFetchDbQuery(
    userId: Int,
    start: Option[Int],
    limit: Int,
    rating: Option[Int] = None,
    businessName: Option[String] = None,
    authorName: Option[String] = None
  ) extends ZolaCCPrinter
}

abstract class UserReviewCassandraDbService extends Actor
    with ActorLogging {

  import UserReviewCassandraDbService._
  import ZolaCassandraDb._
  def receive = {
    case x: UserReviewCreateDbQuery =>
      val currentSender = sender
      UserReviewMapper.insertNewRecord(
        userId        = x.userId,
        transactionId = x.transactionId,
        insertionTime = x.insertionTime,
        rating        = x.rating,
        businessName  = x.businessName,
        callback      = x.callback,
        textInfo      = x.textInfo,
        authorName    = x.authorName
      ).mapTo[ResultSet] map { x => currentSender ! CassandraDbQueryResult(x) }

    case UserReviewFetchDbQuery(userId, start, limit, None, None, None) =>
      UserReviewMapper.fetchAll(userId, start, limit).mapTo[Iterator[UserReviewDbEntry]] pipeTo sender

    case UserReviewFetchDbQuery(userId, start, limit,Some(rating), None, None) =>
      UserReviewMapper.fetchByRating(userId, rating, start, limit).mapTo[Iterator[UserReviewDbEntry]] pipeTo sender

    case UserReviewFetchDbQuery(userId, start, limit, None, Some(businessName), None) =>
      UserReviewMapper.fetchByBusinessName(userId, businessName, start, limit).mapTo[Iterator[UserReviewDbEntry]] pipeTo sender

    case UserReviewFetchDbQuery(userId, start, limit, None, None, Some(authorName)) =>
      UserReviewMapper.fetchByAuthorName(userId, authorName, start, limit).mapTo[Iterator[UserReviewDbEntry]] pipeTo sender
  }
}
