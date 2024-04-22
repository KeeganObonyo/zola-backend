package reviews.zola.core
package db.mysql.service

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{ Actor, ActorLogging }
import akka.pattern.pipe

import reviews.zola._

import core.util.ZolaSecureCCPrinter

import core.db.mysql._

object MysqlDbService {
  case object UserFetchDbQuery

}

class MysqlDbService extends Actor
    with ActorLogging {

  import MysqlDbService._
  def receive = {
    case UserFetchDbQuery =>
      sender() ! APIUser.findAll()
  }
}
