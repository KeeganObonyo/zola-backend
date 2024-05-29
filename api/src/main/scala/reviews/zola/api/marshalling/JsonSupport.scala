package reviews.zola.api
package marshalling

import akka.http.scaladsl.marshallers.sprayjson._

import spray.json._

import reviews.zola._

import core.util.ZolaJsonProtocol

import blogic.UserReviewService._

trait WebJsonSupportT extends DefaultJsonProtocol with SprayJsonSupport {
  import ZolaJsonProtocol._

  implicit val AddReviewFormat   = jsonFormat6(AddReview)
  implicit val AddResponseFormat = jsonFormat2(AddResponse.apply)

  implicit val PlaceIdRequestFormat  = jsonFormat1(PlaceIdRequest)
  implicit val PlaceIdResponseFormat  = jsonFormat1(PlaceIdResponse)

  implicit val ListFeedBackRequestFormat  = jsonFormat2(ListFeedBack)
  implicit val ListReviewRequestFormat  = jsonFormat2(ListReview)

  implicit val ReviewFormat             = jsonFormat5(Review)
  implicit val ListReviewResponseFormat = jsonFormat4(ListReviewResponse)
  implicit val ListFeedBackResponseFormat = jsonFormat3(ListFeedBackResponse)

}

