package reviews.zola.core
package util

import java.net.URL
import scala.concurrent.duration.FiniteDuration
import spray.json._
import org.joda.time.DateTime

object ZolaJsonProtocol {
  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    def write(obj: DateTime): JsValue = JsString(obj.getMillis.toString)
    def read(json: JsValue): DateTime = json match {
      case JsString(str) => new DateTime(str.toLong)
      case _ => throw new DeserializationException("string millis expected")
    }
  }
}