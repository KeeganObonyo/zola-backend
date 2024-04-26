package reviews.zola.core
package db.cassandra.mapper

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

import shapeless.HList

import com.datastax.driver.core.ResultSet

import com.outworkers.phantom.builder.{ ConsistencyBound, OrderBound, Unlimited, WhereBound }
import com.outworkers.phantom.builder.query.{ InsertQuery, SelectQuery }
import com.outworkers.phantom.connectors.RootConnector
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.streams._
import com.outworkers.phantom.streams.iteratee.Iteratee

import reviews.zola._

import core.util.ZolaJsonProtocol._

import core.db.cassandra.service.UserReviewCassandraDbService.UserReviewDbEntry

import core.util.ZolaEnum.ZolaEnvironment
import core.util.ZolaConfig
private[cassandra] abstract class UserReviewMapper extends Table[UserReviewMapper, UserReviewDbEntry] {

  object user_id extends IntColumn with PartitionKey
  object transaction_id extends StringColumn with PrimaryKey
  object insertion_time extends DateTimeColumn
  object rating extends IntColumn with Index
  object business_name extends StringColumn  with Index
  object callback extends OptionalStringColumn
  object text_info extends StringColumn
  object author_name extends StringColumn with Index

  override def tableName = "user_review"

  override def fromRow(row: Row): UserReviewDbEntry = {
    UserReviewDbEntry(
      userId        = user_id(row),
      transactionId = transaction_id(row),
      insertionTime = insertion_time(row),
      rating        = rating(row),
      businessName  = business_name(row),
      callback      = callback(row) match {
        case Some(x) => Some(x.toString)
        case None    => None
      },
      textInfo      = text_info(row),
      authorName    = author_name(row)
    )
  }

  def fetchAll(
    userId: Int,
    start: Option[Int],
    limit: Int
  ): Future[Iterator[UserReviewDbEntry]] = {
    fetchImpl(
      query = select
        .where(_.user_id eqs userId),
      start = start,
      limit = limit
    )
  }

  def fetchByRating(
    userId: Int,
    rating: Int,
    start: Option[Int],
    limit: Int
  ): Future[Iterator[UserReviewDbEntry]] = {
    fetchImpl(
      query = select
        .where(_.user_id eqs userId)
        .and(_.rating eqs rating),
      start = start,
      limit = limit
    )
  }

  def fetchByBusinessName(
    userId: Int,
    businessName: String,
    start: Option[Int],
    limit: Int
  ): Future[Iterator[UserReviewDbEntry]] = {
    fetchImpl(
      query = select
        .where(_.user_id eqs userId)
        .and(_.business_name eqs businessName),
      start = start,
      limit = limit
    )
  }

  def fetchByAuthorName(
    userId: Int,
    authorName: String,
    start: Option[Int],
    limit: Int
  ): Future[Iterator[UserReviewDbEntry]] = {
    fetchImpl(
      query = select
        .where(_.user_id eqs userId)
        .and(_.author_name eqs authorName),
      start = start,
      limit = limit
    )
  }

  def insertNewRecord (
    userId: Int,
    transactionId: String,
    insertionTime: DateTime,
    rating: Int,
    businessName: String,
    callback: Option[String],
    textInfo: String,
    authorName: String
  ): Future[ResultSet] = {
    insertRecordImpl(
      insert
        .value(_.user_id, userId)
        .value(_.transaction_id, transactionId)
        .value(_.insertion_time, insertionTime)
        .value(_.rating, rating)
        .value(_.business_name, businessName)
        .value(_.callback, callback match {
          case Some(x) => Some(x.toString)
          case None    => None
        })
        .value(_.text_info, textInfo)
        .value(_.author_name, authorName)
    )
  }

  private val environment = ZolaConfig.getEnvironment

  protected def insertRecordImpl[Status<: ConsistencyBound, PS <: HList](
    query: InsertQuery[UserReviewMapper, UserReviewDbEntry, Status, PS],
    lifetime: Option[FiniteDuration] = None
  ): Future[ResultSet] = {
    val ttl = environment match {
      case ZolaEnvironment.Development => lifetime
      case _                           => lifetime
    }

    ttl match {
      case Some(x) =>
        query
          .ttl(x.toSeconds.toInt)
          .future()
      case None    => query.future()
    }
  }

  protected def fetchImpl[
    Order <: OrderBound,
    Status <: ConsistencyBound,
    Chain <: WhereBound,
    PS <: HList
  ](
    query: SelectQuery[UserReviewMapper, UserReviewDbEntry, Unlimited, Order, Status, Chain, PS],
    start: Option[Int],
    limit: Int
  ): Future[Iterator[UserReviewDbEntry]] = {
    val startIndex = start match {
      case Some(x) => x
      case None    => 0
    }
    var index       = 0
    val buffer      = ListBuffer.empty[UserReviewDbEntry]
    val queryFuture = query.limit(startIndex + limit).fetchEnumerator run Iteratee.forEach (x => {
      if (index >= startIndex && index < startIndex + limit) buffer += x
      index += 1
    })
    queryFuture map (x => {
      buffer.toList.iterator
    })
  }
}

