package reviews.zola.core
package db.mysql

import java.util. Properties
import java.sql.{Connection, DriverManager, Statement, ResultSet}

import scalikejdbc._
import org.joda.time.{LocalDate, DateTime}

import reviews.zola._

import core.util.{ ZolaSecureCCPrinter, ZolaConfig }

import scalikejdbc._

case class APIUser(
  id: Int,
  username: String,
  apikey: String) extends ZolaSecureCCPrinter {
    override def getSecureFields = Set("apikey")
  }

//Im the implementation. We only read the SQL tables for the Authentication.
object APIUser extends SQLSyntaxSupport[APIUser] {

    Class.forName(ZolaConfig.mysqlDriver)
    val dbc: Connection = DriverManager.getConnection(ZolaConfig.mysqlDbUrl, ZolaConfig.mysqlDbUser, ZolaConfig.mysqlDbPass)

  override val tableName = "api_user"

  override val columns = Seq("id", "username", "api_key")

  def apply(m: ResultName[APIUser])(rs: WrappedResultSet): APIUser = new APIUser(
    id = rs.int(m.id),
    username = rs.string(m.username),
    apikey = rs.string(m.apikey)
  )

  val m = APIUser.syntax("m")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[APIUser] = {
    withSQL {
      select.from(APIUser as m).where.eq(m.id, id)
    }.map(APIUser(m.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[APIUser] = {
    withSQL(select.from(APIUser as m)).map(APIUser(m.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(APIUser as m)).map(rs => rs.long(1)).single.apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[APIUser] = {
    withSQL {
      select.from(APIUser as m).where.append(sqls"${where}")
    }.map(APIUser(m.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(APIUser as m).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }
}