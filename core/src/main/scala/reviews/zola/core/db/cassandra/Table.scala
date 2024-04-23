package reviews.zola.core
package db.cassandra

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
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

import core.util.ZolaEnum.ZolaEnvironment
import core.util.ZolaConfig

private[cassandra] trait ZolaCassandraTableT[Mapper <: CassandraTable[Mapper, Entry], Entry]
    extends CassandraTable[Mapper, Entry]
    with RootConnector
{
  private val environment = ZolaConfig.getEnvironment

  protected def insertRecordImpl[Status<: ConsistencyBound, PS <: HList](
    query: InsertQuery[Mapper, Entry, Status, PS],
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
    query: SelectQuery[Mapper, Entry, Unlimited, Order, Status, Chain, PS],
    start: Option[Int],
    limit: Int
  ): Future[Iterator[Entry]] = {
    val startIndex = start match {
      case Some(x) => x
      case None    => 0
    }
    var index       = 0
    val buffer      = ListBuffer.empty[Entry]
    val queryFuture = query.limit(startIndex + limit).fetchEnumerator run Iteratee.forEach (x => {
      if (index >= startIndex && index < startIndex + limit) buffer += x
      index += 1
    })
    queryFuture map (x => {
      buffer.toList.iterator
    })
  }
}
