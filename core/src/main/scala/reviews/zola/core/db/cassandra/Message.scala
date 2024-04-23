package reviews.zola.core
package db.cassandra

import com.datastax.driver.core.ResultSet

case class CassandraDbQueryResult(status: Boolean)

private[cassandra] object CassandraDbQueryResult {
  def apply(result: ResultSet) = new CassandraDbQueryResult(result.isExhausted)
}

