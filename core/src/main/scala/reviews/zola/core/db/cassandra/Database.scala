package reviews.zola.core
package db.cassandra

import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._

import mapper._

private[cassandra] object ZolaCassandraDb extends ZolaCassandraDb(ZolaCassandraDbConnector.connector)

private[cassandra] sealed class ZolaCassandraDb(override val connector: CassandraConnection) extends Database[ZolaCassandraDb](connector) {

  object UserReviewMapper extends UserReviewMapper with connector.Connector
}