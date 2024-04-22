package reviews.zola.core.util

import com.typesafe.config.ConfigFactory

import ZolaEnum._

object ZolaConfig extends ZolaConfigT {
  protected def getEnvironmentImpl = System.getenv("ZOLA_ENV")
}

private[util] trait ZolaConfigT {

val config = ConfigFactory.load
  config.checkValid(ConfigFactory.defaultReference)


  protected def getEnvironmentImpl: String
  protected val environment = getEnvironmentImpl

  def getEnvironment : ZolaEnvironment.Value = environment match {
    case "dev"     => ZolaEnvironment.Development
    case "prod"    => ZolaEnvironment.Production
    case x         => throw new Exception("Unexpected environment value: " + x)
  }

  // API
  val apiInterface = config.getString("zola.web-service.api.interface")
  val apiPort      = config.getInt("zola.web-service.api.port")

  //Actor TimeOut
  val serviceTimeout    = ZolaUtil.parseFiniteDuration(config.getString("zola-backend.actor-timeout.service")).get
  val webRequestTimeout = ZolaUtil.parseFiniteDuration(config.getString("zola-backend.actor-timeout.web-request")).get

  val mysqlDbAuthenticationCacheUpdateFrequency = ZolaUtil.parseFiniteDuration(config.getString("zola.db.mysql.cache.update-frequency.authentication")).get

  val mysqlDbHost  = config.getString("zola.db.mysql.host")
  val mysqlDbPort  = config.getInt("zola.db.mysql.port")
  val mysqlDbUser  = config.getString("zola.db.mysql.user")
  val mysqlDbPass  = config.getString("zola.db.mysql.pass")
  val mysqlDbName  = config.getString("zola.db.mysql.name")

  val mysqlDbPoolMaxObjects   = config.getInt("zola.db.mysql.pool.max-objects")
  val mysqlDbPoolMaxIdle      = config.getInt("zola.db.mysql.pool.max-idle")
  val mysqlDbPoolMaxQueueSize = config.getInt("zola.db.mysql.pool.max-queue-size")
}
