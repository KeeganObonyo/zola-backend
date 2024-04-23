package reviews.zola.core
package db.cassandra

import com.outworkers.phantom.connectors.ContactPoints
import com.datastax.driver.core.ProtocolOptions

import reviews.zola._

import core.util.ZolaEnum.ZolaEnvironment
import core.util.ZolaConfig

private[cassandra] trait CassandraDbConnectorT {

  protected def getPort: Int
  protected def getHosts: List[String]
  protected def getKeySpace: String

  private var builder = ContactPoints(getHosts, getPort)
    .withClusterBuilder(_.withCompression(ProtocolOptions.Compression.LZ4))
    .withClusterBuilder(_.withoutJMXReporting())
    .withClusterBuilder(_.withoutMetrics())

  ZolaConfig.getEnvironment match {
    case ZolaEnvironment.Development =>
    case x =>
      val username = ZolaConfig.cassandraUsername
      val password = ZolaConfig.cassandraPassword
      builder = builder.withClusterBuilder(_.withCredentials(username, password))
  }

  lazy val connector = builder.keySpace(
    name     = getKeySpace,
    autoinit = false
  )
}

private[cassandra] object ZolaCassandraDbConnector extends CassandraDbConnectorT {
  override def getPort     = ZolaConfig.cassandraPort
  override def getHosts    = ZolaConfig.cassandraHosts
  override def getKeySpace = ZolaConfig.cassandraKeySpace
}
