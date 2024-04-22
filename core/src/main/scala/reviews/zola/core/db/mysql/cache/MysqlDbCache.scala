package reviews.zola.core
package db.mysql.cache

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

import akka.actor.{ ActorRef, ActorRefFactory }
import akka.actor.{ Actor, ActorLogging, Cancellable, Props }

import reviews.zola._

import core.db.mysql.service.MysqlDbService
import core.util.ZolaInstanceManagerT

private[cache] case object UpdateCacheRequest

private[cache] trait MysqlDbCacheManagerT extends ZolaInstanceManagerT[ActorRef] {
  def initialize(cache: ActorRef) = setInstance(cache)
}

private[cache] trait MysqlDbCacheT extends Actor with ActorLogging {

  protected val updateFrequency: FiniteDuration

  val mysqlDbService       = createMysqlDbService
  def createMysqlDbService = context.actorOf(Props[MysqlDbService])

  final override def receive = specificMessageHandler orElse genericMessageHandler
  protected def specificMessageHandler: Receive = Map.empty

  private def genericMessageHandler: Receive = {
    case UpdateCacheRequest =>
      log.info("processing UpdateCacheRequest")
      updateCacheRequestImpl
      scheduleUpdate
  }

  protected def updateCacheRequestImpl: Unit

  override def preStart() = {
    log.info("preStart called. Update the cache")
    self ! UpdateCacheRequest
  }

  override def postStop() = {
    if(currentScheduler != null && !currentScheduler.isCancelled) currentScheduler.cancel()
  }

  private var currentScheduler: Cancellable = null
  private def scheduleUpdate: Unit = {
    currentScheduler = context.system.scheduler.scheduleOnce(
      updateFrequency,
      self,
      UpdateCacheRequest
    )
  }
}
  
