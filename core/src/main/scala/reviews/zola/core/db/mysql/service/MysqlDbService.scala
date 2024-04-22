package reviews.zola.core
package db.mysql

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{ Actor, ActorLogging }
import akka.pattern.pipe

import reviews.zola._

import core.util.ZolaSecureCCPrinter

object ZolaMysqlDbService {
  case object UserFetchDbQuery

}

class ZolaMysqlDbService extends Actor
    with ActorLogging {

  import ZolaMysqlDbService._
  def receive = {
    case UserFetchDbQuery =>
      sender() ! APIUser.findAll()
  }
}
