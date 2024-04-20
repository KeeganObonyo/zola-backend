package io.zola.zolabackend.core.api
package marshalling

import akka.http.scaladsl.marshallers.sprayjson._

import spray.json._

import io.zola.zola-backend._

import blogic.util._

import core.util.ZolaBackendEnum._

import blogic.Blogic._

trait WebJsonSupportT extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object ServiceStatusJsonFormat extends RootJsonFormat[ServiceStatus.Value] {
    def write(obj: ServiceStatus.Value): JsValue = JsString(obj.toString)
    def read(json: JsValue): ServiceStatus.Value = json match {
      case JsString(str) => ServiceStatus.withName(str)
      case _ => throw new DeserializationException("Enum string expected")
    }
  }

  implicit val AddReviewRequestFormat  = jsonFormat1(AddReviewRequest)
  implicit val AddReviewResponseFormat = jsonFormat2(AddReviewResponse)

  implicit val ListReviewRequestFormat  = jsonFormat1(ListReviewRequest)
  implicit val ListReviewResponseFormat = jsonFormat2(ListReviewResponse)

}

