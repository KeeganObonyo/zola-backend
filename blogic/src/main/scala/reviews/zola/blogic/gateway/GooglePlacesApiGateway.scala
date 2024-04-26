package reviews.zola.blogic
package gateway

import java.net.URL

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

import akka.actor.{ Actor, ActorLogging, Props }
import akka.http.scaladsl.model._
import akka.http.scaladsl.marshalling.Marshal
import akka.pattern.ask
import akka.util.Timeout

import spray.json._

import org.joda.time.DateTime

import reviews.zola._

import core.http.ZolaHttpClientT
import core.util.{ ZolaCCPrinter, ZolaUtil, ZolaConfig, ZolaEnum }
import ZolaEnum.ServiceStatus

import GooglePlacesApiGatewayMarshalling._

private[gateway] object GooglePlacesApiGateway {

  case class FindPlaceIdRequest(
    businessName: String
  ) extends ZolaCCPrinter

  case class FindPlaceIdResponse(
    placeId: Option[String],
    status: ServiceStatus.Value
  ) extends ZolaCCPrinter

  case class FindPlaceDetailRequest(
    placeId: String
  ) extends ZolaCCPrinter

  case class Review(
    authorName: String,
    rating: Int,
    callback: Option[String],
    textInfo: Option[String],
    insertionTime: DateTime
  ) extends ZolaCCPrinter

  case class FindPlaceDetailResponse(
    reviews: List[Review],
    status: ServiceStatus.Value,
    rating: Option[Float]
  ) extends ZolaCCPrinter

}

private[gateway] class GooglePlacesApiGateway extends Actor
    with ZolaHttpClientT
    with GooglePlacesApiGatewayJsonSupportT
    with ActorLogging {

  implicit val system             = context.system

  implicit val timeout            = Timeout(ZolaConfig.webRequestTimeout)

  private val findPlacesUrl       = ZolaConfig.googlePlacesFindUrl
  private val placesDetailUrl     = ZolaConfig.googlePlacesDetailUrl
  private val googlePlacesAPIKey  = ZolaConfig.googlePlacesAPIKey

  import GooglePlacesApiGateway._
  override def receive = {
    case req: FindPlaceIdRequest  =>
      log.info("processing " + req)
      val currentSender = sender
      val BusinessName = req.businessName
      val url = s"$findPlacesUrl?fields=&input=$BusinessName&inputtype=textquery&key=$googlePlacesAPIKey"

      val http = HttpRequest(
      method = HttpMethods.GET,
      uri    = url,
      )
      val sendFut = for {
      response <- sendHttpRequest(http)
      } yield {
      log.info(s"Processed request to [$url]:\n--Response---:\n$response")
      response
      }

      sendFut onComplete {
        case Success(response) =>
          response.status.isSuccess match {
            case true =>
              try {
                val brokerResponse = response.data.parseJson.convertTo[GoogleFindPlaceIdResponse]
                brokerResponse.candidates.head match {
                  case PlaceId(id) =>
                    currentSender ! FindPlaceIdResponse(
                      placeId = Some(id),
                      status  = ServiceStatus.Success
                    )
                  case _             =>
                    currentSender ! FindPlaceIdResponse(
                      placeId = None,
                      status  = ServiceStatus.Error
                    )
                }
              }
              catch {
                case ex: JsonParser.ParsingException =>
                  log.info(s"Error while processing response for $req: $response")
                  val errorResponse = response.data.parseJson.convertTo[GoogleErrorResponse]
                  currentSender ! FindPlaceIdResponse(
                    placeId = None,
                    status  = ServiceStatus.Error
                  )
              }
            case false =>
              log.info(s"Received Http error response while processing $req: $response")
              currentSender ! FindPlaceIdResponse(
                placeId = None,
                status  = ServiceStatus.Failed
              )
          }
        case Failure(error)    =>
          log.info(s"Error $error while processing $req")
          currentSender ! FindPlaceIdResponse(
            placeId = None,
            status  = ServiceStatus.Failed
          )
      }
    case req: FindPlaceDetailRequest  =>
      log.info("processing " + req)
      val currentSender = sender

      val PlaceId = req.placeId
      val url = s"$placesDetailUrl?fields=name%2Crating%2Creviews&reviews_sort=newest&place_id=$PlaceId&key=$googlePlacesAPIKey"

      val http = HttpRequest(
      method = HttpMethods.GET,
      uri    = url,
      )
      val sendFut = for {
      response <- sendHttpRequest(http)
      } yield {
      log.info(s"Processed request to [$url]:\n--Response---:\n$response")
      response
      }

      sendFut onComplete {
        case Success(response) =>
          response.status.isSuccess match {
            case true =>
              try {
                val brokerResponse = response.data.parseJson.convertTo[GoogleFindPlaceDetailResponse]

                val reviewList = 
                  for {
                      gReview <- brokerResponse.result.reviews
                      review = Review(
                          authorName    = gReview.author_name,
                          rating        = gReview.rating,
                          textInfo      = gReview.text,
                          callback      = None,
                          insertionTime = ZolaUtil.epochToDate(gReview.time)
                      )
                  } yield review
                currentSender ! FindPlaceDetailResponse(
                  reviews = reviewList,
                  status  = ServiceStatus.Success,
                  rating  = Some(brokerResponse.result.rating)
                )
              }
              catch {
                case ex: JsonParser.ParsingException =>
                  log.info(s"Error while processing response for $req: $response")
                  val errorResponse = response.data.parseJson.convertTo[GoogleErrorResponse]
                  currentSender ! FindPlaceDetailResponse(
                    reviews   = List[Review](),
                    status    = ServiceStatus.Error,
                    rating    = None
                  )
              }
            case false =>
              log.info(s"Received Http error response while processing $req: $response")
              currentSender ! FindPlaceDetailResponse(
                reviews   = List[Review](),
                status    = ServiceStatus.Failed,
                rating    = None
              )
          }
        case Failure(error)    =>
          log.info(s"Error $error while processing $req")
          currentSender ! FindPlaceDetailResponse(
            reviews   = List[Review](),
            status    = ServiceStatus.Failed,
            rating    = None
          )
      }
    }

}
