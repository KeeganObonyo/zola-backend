package reviews.zola.blogic
package gateway

import akka.http.scaladsl.marshallers.sprayjson._

import spray.json._

import reviews.zola._

import core.util.{ ZolaCCPrinter, ZolaEnum }
import ZolaEnum.ServiceStatus

private[gateway] object GooglePlacesApiGatewayMarshalling {

  case class PlaceId(
    place_id: String
  ) extends ZolaCCPrinter

  case class GoogleFindPlaceIdResponse(
    candidates: List[PlaceId],
    status: String
  ) extends ZolaCCPrinter

  case class GoogleReview(
    author_name: String,
    author_url: String,
    profile_photo_url: String,
    rating: Int,
    relative_time_description: String,
    text: Option[String],
    time: Long,
    translated: Boolean
  ) extends ZolaCCPrinter

  case class Result(
    name: String,
    rating: Double,
    reviews: List[GoogleReview]
  ) extends ZolaCCPrinter

  case class GoogleFindPlaceDetailResponse(
    html_attributions: List[String],
    result: Result,
    status: String
  ) extends ZolaCCPrinter

  case class GoogleErrorResponse(
    candidates: List[PlaceId],
    error_message: String,
    status: String
  ) extends ZolaCCPrinter

  trait GooglePlacesApiGatewayJsonSupportT extends SprayJsonSupport
      with DefaultJsonProtocol {
    
    implicit val ReviewFormat                        = jsonFormat8(GoogleReview)
    implicit val ResultFormat                        = jsonFormat3(Result)
    implicit val GoogleFindPlaceDetailResponseFormat = jsonFormat3(GoogleFindPlaceDetailResponse)

    implicit val PlaceIdFormat                   = jsonFormat1(PlaceId)
    implicit val GoogleFindPlaceIdResponseFormat = jsonFormat2(GoogleFindPlaceIdResponse)

    implicit val GoogleErrorResponseFormat       = jsonFormat3(GoogleErrorResponse)
  }

}