package reviews.zola.core
package util

import java.net.URL
import scala.concurrent.duration.FiniteDuration
import spray.json._
import org.joda.time.DateTime

import ZolaEnum._

object ZolaJsonProtocol {
  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    def write(obj: DateTime): JsValue = JsString(obj.getMillis.toString)
    def read(json: JsValue): DateTime = json match {
      case JsString(str) => new DateTime(str.toLong)
      case _ => throw new DeserializationException("string millis expected")
    }
  }

  implicit object ServiceStatusJsonFormat extends RootJsonFormat[ServiceStatus.Value] {
    def write(obj: ServiceStatus.Value): JsValue = JsString(obj.toString)
    def read(json: JsValue): ServiceStatus.Value = json match {
      case JsString(str) => ServiceStatus.withName(str)
      case _ => throw new DeserializationException("Enum string expected")
    }
  }
}