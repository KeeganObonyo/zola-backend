package reviews.zola.api
package marshalling

import akka.http.scaladsl.marshallers.sprayjson._

import spray.json._

import reviews.zola._

import core.util.ZolaJsonProtocol

import blogic.Blogic._

trait WebJsonSupportT extends DefaultJsonProtocol with SprayJsonSupport {
  import ZolaJsonProtocol._

  implicit val AddReviewRequestFormat  = jsonFormat7(AddReviewRequest)
  implicit val AddReviewResponseFormat = jsonFormat2(AddReviewResponse)

  implicit val ListReviewRequestFormat  = jsonFormat1(ListReviewRequest)
  implicit val ReviewFormat             = jsonFormat5(Review)
  implicit val ListReviewResponseFormat = jsonFormat5(ListReviewResponse)

}

