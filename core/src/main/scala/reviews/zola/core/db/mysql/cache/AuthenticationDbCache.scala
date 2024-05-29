package reviews.zola.core
package db.mysql.cache

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout

import reviews.zola._

import core.db.mysql.service.MysqlDbService
import MysqlDbService._
import core.util.{ ZolaCCPrinter, ZolaEnum, ZolaLog, ZolaUtil, ZolaConfig }

import core.db.mysql._

object AuthenticationDbCache extends AuthenticationDbCacheT {

  private[cache] case class AuthKey(
    username: String,
    apikey: String
  ) extends ZolaCCPrinter

}

trait AuthenticationDbCacheT extends MysqlDbCacheManagerT with ZolaLog {

  import AuthenticationDbCache._

  def authenticate(
    username: String,
    apikey: String
  ): Option[APIUser] = authenticationMap.get(AuthKey(
    username = username.toLowerCase,
    apikey   = apikey
  ))

  def props = Props(classOf[AuthenticationDbCache], this)

  private var authenticationMap = Map[AuthKey, APIUser]()
  private[cache] def setAuthenticationMap(map: Map[AuthKey, APIUser]): Unit = {
    authenticationMap = map
  }
}

private[core] class AuthenticationDbCache(
  manager: AuthenticationDbCacheT
) extends MysqlDbCacheT {

  implicit val timeout         = Timeout(ZolaConfig.serviceTimeout)
  override val updateFrequency = ZolaConfig.mysqlDbAuthenticationCacheUpdateFrequency

  import AuthenticationDbCache._
  override protected def updateCacheRequestImpl = {
    (mysqlDbService ? UserFetchDbQuery).mapTo[List[APIUser]] map { entries => {
      manager.setAuthenticationMap(
        entries.foldLeft(Map[AuthKey, APIUser]()) {
          case (m, entry) =>
            m.updated(
                AuthKey(
                username = entry.username.toLowerCase,
                apikey   = entry.apikey
                ),
                entry
            )
        }
      )
    }}
  }
}
