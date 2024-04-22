package reviews.zola.core
package db.mysql

import scalikejdbc._
import org.joda.time.{LocalDate, DateTime}

case class APIUser(
  id: Int,
  username: String,
  apikey: String) {
}

//Im the implementation. We only read the SQL tables for the Authentication.
object APIUser extends SQLSyntaxSupport[APIUser] {

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