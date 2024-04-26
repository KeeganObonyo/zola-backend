package reviews.zola.core.util

import java.net.{ URL, URLEncoder }
import java.security.MessageDigest

import java.util.Date
import java.text.SimpleDateFormat

import org.joda.time.DateTime

import scala.concurrent.duration.{ Duration, FiniteDuration, MILLISECONDS }

object ZolaUtil {
  private[util] def getCaseClassString(cc: AnyRef) : String = {
    (cc.getClass.getDeclaredFields map (f  => {
      f.setAccessible(true)
      f.getName + "=" + getAnyString(f.get(cc), "None")
    })).mkString(cc.getClass.getSimpleName + "[", ";", "]")
  }
  private[util] def getSecureCaseClassString(
    cc: AnyRef,
    secureFields: Set[String]
  ) : String = {
    (cc.getClass.getDeclaredFields map (f  => {
      f.setAccessible(true)
      val name  = f.getName
      val field = secureFields.contains(name) match {
        case true  => "Restricted"
        case false => getAnyString(f.get(cc), "None")
      }
      name + "=" + field
    })).mkString(cc.getClass.getSimpleName + "[", ";", "]")
  }
  def getAnyString(any: Any, default: String = "") : String =
    any match {
      case Some(x) => x.toString
      case None    => default
      case x       => x.toString
  }

  def parseFiniteDuration(str: String) : Option[FiniteDuration] = {
    try {
      Some(Duration(str)).collect { case d: FiniteDuration => d }
    } catch {
      case ex: NumberFormatException => None
    }
  }

  def epochToDate(epochMillis: Long): DateTime = {
    val df:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    DateTime.parse(df.format(epochMillis))
  }

  def sha256Hash(value: String): String = MessageDigest.getInstance("SHA-256").digest(value.getBytes).map("%02x".format(_)).mkString

  def md5Hash(value: String): String = MessageDigest.getInstance("MD5").digest(value.getBytes).map("%02x".format(_)).mkString
}