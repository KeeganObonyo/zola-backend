package reviews.zola.core
package db.cassandra.mapper

import java.net.URL

import scala.concurrent.Future

import org.joda.time.DateTime

import com.outworkers.phantom.dsl._

import reviews.zola._

import core.db.cassandra.service.UserReviewCassandraDbService._
import core.db.cassandra.ZolaCassandraTableT

private[mapper] sealed trait UserReviewMapperT extends ZolaCassandraTableT[UserReviewMapper, UserReviewDbEntry] {

  object user_id extends IntColumn with PartitionKey
  object transaction_id extends StringColumn with PrimaryKey
  object insertion_time extends DateTimeColumn
  object rating extends IntColumn with Index
  object business_name extends StringColumn  with Index
  object callback extends OptionalStringColumn
  object text_info extends StringColumn
  object author_name extends StringColumn with Index
}

private[cassandra] abstract class UserReviewMapper extends UserReviewMapperT {

  override val tableName = "user_review"

  override def fromRow(row: Row): UserReviewCreateDbQuery = {
    UserReviewDbEntry(
      userId        = user_id(row),
      transactionId = transaction_id(row),
      insertionTime = insertion_time(row),
      rating        = rating(row),
      businessName  = business_name(row),
      callback      = callback(row) match {
        case Some(x) => Some(x)
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
}

