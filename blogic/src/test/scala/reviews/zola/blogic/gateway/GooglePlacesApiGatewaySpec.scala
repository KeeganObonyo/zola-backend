package reviews.zola.blogic
package gateway

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.Props
import akka.http.scaladsl.model._
import akka.testkit.TestProbe

import spray.json._

import org.joda.time.DateTime

import reviews.zola._

import blogic.test._

import reviews.zola.core.http.ZolaHttpClientResponse
import reviews.zola.core.util.{ ZolaEnum, ZolaUtil }
import ZolaEnum._

import UserReviewService._
import GooglePlacesApiGateway._
import GooglePlacesApiGatewayMarshalling._

class GooglePlacesApiGatewaySpec extends TestHttpStringEndpointT
    with  GooglePlacesApiGatewayJsonSupportT {

  val gateway = system.actorOf(Props(new GooglePlacesApiGateway {
    override def sendHttpRequest(req: HttpRequest) =
      Future.successful(getStringHttpResponse(req))
  }))


    "given an FindPlaceIdRequest" should {
        "Process and give a valid response" in {
        gateway ! FindPlaceIdRequest(
            businessName = "Kilimanjaro Restaurant"
        )

        expectMsg(FindPlaceIdResponse(
            placeId = Some("ChIJ3wPNloERLxgRZHhUmyA2tEQ"),
            status  = ServiceStatus.Success
        ))
    }}

    "given an FindPlaceDetailRequest" should {
        "Process and give a valid response" in {
        gateway ! FindPlaceDetailRequest(
            placeId = "ChIJ3wPNloERLxgRZHhUmyA2tEQ"
        )

        expectMsg(FindPlaceDetailResponse(
            reviews = List(Review(
                insertionTime   = ZolaUtil.epochToDate(1714301484),
                rating          = 4,
                callback        = None,
                textInfo        = Some("The setting is nice and clean. The service superb. I feel the biryani was kinda flat though."),
                authorName      = "Dennis Okoth Angira Manogo"
            )),
            status  = ServiceStatus.Success,
            rating  = Some(4.3)
        ))
    }}

  def getStringHttpResponseImpl(
    data: String,
    uri: Uri
  ) = {
    uri.toString match {
      case "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?fields=&input=KilimanjaroRestaurant&inputtype=textquery&key=AIzaSyD2JOjywKAF0oIawToVVtdwLXXsQu8EWWg" =>
        ZolaHttpClientResponse(
            StatusCodes.OK,
            GoogleFindPlaceIdResponse(
                candidates = List(PlaceId(
                    place_id = "ChIJ3wPNloERLxgRZHhUmyA2tEQ"
                )),
                status     = "OK"
            ).toJson.compactPrint
        )
      case "https://maps.googleapis.com/maps/api/place/details/json?fields=name%2Crating%2Creviews&reviews_sort=newest&place_id=ChIJ3wPNloERLxgRZHhUmyA2tEQ&key=AIzaSyD2JOjywKAF0oIawToVVtdwLXXsQu8EWWg" =>
        ZolaHttpClientResponse(
            status = StatusCodes.OK,
            data   = """{
            "html_attributions": [],
            "result": {
                "name": "Kilimanjaro Jamia, Kimathi Street",
                "rating": 4.3,
                "reviews": [
                    {
                        "author_name": "Dennis Okoth Angira Manogo",
                        "author_url": "https://www.google.com/maps/contrib/110697026841758951836/reviews",
                        "language": "en",
                        "original_language": "en",
                        "profile_photo_url": "https://lh3.googleusercontent.com/a-/ALV-UjUhYH2TrRZPNnkp0ieXWquogahxqyKoCwDjGdmo8zsXc6WVT4In=s128-c0x00000000-cc-rp-mo-ba4",
                        "rating": 4,
                        "relative_time_description": "in the last week",
                        "text": "The setting is nice and clean. The service superb. I feel the biryani was kinda flat though.",
                        "time": 1714301484,
                        "translated": false
                }]
            },
            "status": "OK"
                
            }"""
        )
      case _ =>
        ZolaHttpClientResponse(
          StatusCodes.BadRequest,
          "Invalid request"
        )
    }
  }
}
