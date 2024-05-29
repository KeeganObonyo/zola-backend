package reviews.zola.core.util

import scala.collection.JavaConverters._
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
    case x         => ZolaEnvironment.Development
  }

  // API
  val apiInterface = config.getString("zola.web-service.api.interface")
  val apiPort      = config.getInt("zola.web-service.api.port")

  //Actor TimeOut
  val serviceTimeout    = ZolaUtil.parseFiniteDuration(config.getString("zola-backend.actor-timeout.service")).get
  val webRequestTimeout = ZolaUtil.parseFiniteDuration(config.getString("zola-backend.actor-timeout.web-request")).get
  
  val mysqlDbAuthenticationCacheUpdateFrequency = ZolaUtil.parseFiniteDuration(config.getString("zola.db.mysql.cache.update-frequency.authentication")).get

  val mysqlDbUrl   = config.getString("zola.db.default.url")
  val mysqlDriver  = config.getString("zola.db.default.driver")
  val mysqlDbUser  = config.getString("zola.db.default.user")
  val mysqlDbPass  = config.getString("zola.db.default.pass")

    // db 
  // Cassandra
  val cassandraUsername      = config.getString("zola.db.cassandra.username")
  val cassandraPassword      = config.getString("zola.db.cassandra.password")
  val cassandraHosts         = config.getStringList("zola.db.cassandra.hosts").asScala.toList
  val cassandraPort          = config.getInt("zola.db.cassandra.port")
  val cassandraKeySpace      = config.getString("zola.db.cassandra.key-space")

  //Google Places
  val googlePlacesFindUrl   = config.getString("zola.googleplaces.api.place.url")
  val googlePlacesDetailUrl = config.getString("zola.googleplaces.api.detail.url")
  val googlePlacesAPIKey    = config.getString("zola.googleplaces.api.key")

  val zolaKeyStore = config.getString("zola.key.store")
}
