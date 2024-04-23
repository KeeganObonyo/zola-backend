package com.africasTalking.elmer.core
package db.cassandra.service

import java.net.URL

import java.util.UUID

import scala.language.postfixOps

import scala.concurrent.duration._

import akka.actor.Props

import org.joda.time.DateTime

import io.atlabs._

import horus.core.db.cassandra.CassandraDbQueryResult

import com.africasTalking._

import elmer.core.db.cassandra._
import elmer.core.test._
import elmer.core.util.ElmerEnum._

import FoodOrderCassandraDbService._

class FoodOrderCassandraDbServiceSpec extends TestServiceT {

  val dbService     = system.actorOf(Props[FoodOrderCassandraDbService])
  val transactionId = UUID.randomUUID.toString
  val name          = FoodName.Ugali
  val quantity      = 3
  val callbackUrl   = Some(new URL("http://www,test.com.com/callback"))
  val status        = FoodOrderStatus.Accepted
  val description   = "Order accepted for processing"
  val orderState1   = FoodOrderStatus.Accepted
  val orderState2   = FoodOrderStatus.Delivered
  val userId        = 1
  val time          = DateTime.now

  "FoodOrderCassandraDbService" must {
    "insert a new food order correctly into the database" in {
      dbService ! FoodOrderCreateDbQuery(
          transactionId = transactionId,
          userId        = userId,
          insertionTime = time,
          name          = name,
          quantity      = quantity,
          callbackUrl   = callbackUrl
        )
      expectMsg(new CassandraDbQueryResult(true))
    }    
    "fetch food orders when filtered by userId" in {
      dbService ! FoodOrderFetchDbQuery(
        userId        = userId,
        start         = Some(0),
        limit         = 1
      )
      val result = expectMsgClass(
        5 seconds,
        classOf[Iterator[FoodOrderDbEntry]]
      )
      result.hasNext should be (true)
      val dbEntry = result.next
      dbEntry.transactionId should be (transactionId)
      dbEntry.userId should be (userId)
      dbEntry.name should be (name)
      dbEntry.quantity should be (quantity)
      dbEntry.callbackUrl should be (callbackUrl)
    }
    "insert a new confirmed food order correctly into the database" in {
      dbService ! FoodOrderStatusCreateDbQuery(
          transactionId = transactionId,
          status        = status,
          description   = description
        )
      expectMsg(new CassandraDbQueryResult(true))
    }
    "find a confirmedfood order by transactionId" in {
      dbService ! FoodOrderStatusFindDbQuery(
            transactionId = transactionId
      )
      val result = expectMsgClass(
        5 seconds,
        classOf[Option[FoodOrderStatusDbEntry]]
      )
      result should not be (None)
      val dbEntry = result.get
      dbEntry.transactionId should be (transactionId)
      dbEntry.status should be (status)
      dbEntry.description should be (description)
    }
  }
}
