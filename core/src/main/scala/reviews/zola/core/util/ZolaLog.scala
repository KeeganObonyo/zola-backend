package reviews.zola.core.util

import scala.concurrent.duration.{ Duration, FiniteDuration }

import org.slf4j.LoggerFactory

trait ZolaLog {
  def log = LoggerFactory.getLogger(this.getClass)
}
//For better printing of case classes during logging
trait ZolaCCPrinter {
  override def toString = ZolaUtil.getCaseClassString(this)
}

trait ZolaSecureCCPrinter {
  def getSecureFields: Set[String]
  override def toString = ZolaUtil.getSecureCaseClassString(this, getSecureFields)
}