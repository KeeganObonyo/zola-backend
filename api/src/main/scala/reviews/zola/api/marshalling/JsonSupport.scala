package reviews.zola.api
package marshalling

import akka.http.scaladsl.marshallers.sprayjson._

import spray.json._

import reviews.zola._

import core.util.ZolaJsonProtocol

import blogic.Blogic._

trait WebJsonSupportT extends DefaultJsonProtocol with SprayJsonSupport {
  import ZolaJsonProtocol._

  implicit val AddReviewFormat   = jsonFormat6(AddReview)
  implicit val AddResponseFormat = jsonFormat2(AddResponse.apply)

  implicit val ListReviewRequestFormat  = jsonFormat2(ListReview)
  implicit val ReviewFormat             = jsonFormat5(Review)
  implicit val ListReviewResponseFormat = jsonFormat5(ListReviewResponse)

}

