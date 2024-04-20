package io.zola.zolabackend.core.util

import scala.concurrent.duration.{ Duration, FiniteDuration }

import org.slf4j.LoggerFactory

trait ZolaLog {
  def log = LoggerFactory.getLogger(this.getClass)
}
//For better printing of case classes during logging
trait ZolaCCPrinter {
  override def toString = ZolaUtil.getCaseClassString(this)
}

object ZolaUtil {
  private[util] def getCaseClassString(cc: AnyRef) : String = {
    (cc.getClass.getDeclaredFields map (f  => {
      f.setAccessible(true)
      f.getName + "=" + getAnyString(f.get(cc), "None")
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
}