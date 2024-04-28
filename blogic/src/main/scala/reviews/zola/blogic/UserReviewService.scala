package reviews.zola.blogic

// import scala.languageFeature.existentials
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.util.Timeout

import org.joda.time.DateTime

import reviews.zola._

import core.util.{ ZolaEnum, ZolaCCPrinter, ZolaConfig }
import ZolaEnum.ServiceStatus

import core.db.cassandra.CassandraDbQueryResult
import core.db.cassandra.service.UserReviewCassandraDbService

import gateway.GooglePlacesApiGateway

object UserReviewService {
    case class AddReviewRequest(
      userId: Int,
      rating: Int,
      businessName: String,
      callback: Option[String],
      textInfo: Option[String],
      authorName: String
    ) extends ZolaCCPrinter
    case class AddReviewResponse(
      description: String,
      status: ServiceStatus.Value
    ) extends ZolaCCPrinter
    case class ListReviewRequest(
      businessName: String,
      userId: Int
    ) extends ZolaCCPrinter
    case class Review(
      insertionTime: DateTime,
      rating: Int,
      callback: Option[String],
      textInfo: Option[String],
      authorName: String
    ) extends ZolaCCPrinter
    
    case class ListReviewResponse(
      reviews: Option[List[Review]],
      businessName: String,
      status: ServiceStatus.Value,
      rating: Option[Double]
    ) extends ZolaCCPrinter
}

class UserReviewService extends Actor with ActorLogging {

  implicit val system  = context.system
  implicit val timeout = Timeout(ZolaConfig.serviceTimeout)

  private lazy val userReviewCassandraDbService = createUserReviewCassandraDbService
  def createUserReviewCassandraDbService        = context.actorOf(Props[UserReviewCassandraDbService])

  private val gateway  = createGateway
  def createGateway    = context.actorOf(Props[GooglePlacesApiGateway])

  def retrieveTxId: String = java.util.UUID.randomUUID.toString

  import GooglePlacesApiGateway._
  import UserReviewCassandraDbService._
  import UserReviewService._
  def receive: Receive = {
    case req: AddReviewRequest           =>
      log.info("processing " + req)
      val currentSender = sender()

      (userReviewCassandraDbService ? UserReviewCreateDbQuery(
        transactionId = retrieveTxId,
        userId        = req.userId,
        insertionTime = DateTime.now,
        rating        = req.rating,
        businessName  = req.businessName,
        callback      = req.callback,
        textInfo      = req.textInfo,
        authorName    = req.authorName
      )).mapTo[CassandraDbQueryResult] onComplete { result =>
        result match {
          case Success(x)  =>
            x.status match {
              case true  =>
                currentSender ! AddReviewResponse(
                  "Review appended successfully",
                  ServiceStatus.Success
                )
              case false =>
                log.info(s"Manual intervention required: Error while updating Cassandra for review [$req]")
                currentSender ! AddReviewResponse(
                  "Error uploading review",
                  ServiceStatus.Failed
                )
            }
          case Failure(error) =>
            log.info(s"Manual intervention required: Error while updating Cassandra for review [$req] $error")
            currentSender ! AddReviewResponse(
              "Error uploading review",
              ServiceStatus.Failed
            )
        }
      }

    case req: ListReviewRequest        =>
      log.info("processing " + req)
      val currentSender = sender()

      (gateway ? FindPlaceIdRequest(
        businessName  = req.businessName
      )).mapTo[FindPlaceIdResponse] onComplete {
        case Success(response) =>
          (gateway ? FindPlaceDetailRequest(
            placeId = response.placeId.get
          )).mapTo[FindPlaceDetailResponse] onComplete {
            case Success(response) =>
              val future        = {
                for {
                  lookupsIter  <- (userReviewCassandraDbService ? UserReviewFetchDbQuery(
                    req.userId, None, 1000, None, None, None
                  )).mapTo[Iterator[UserReviewDbEntry]]
                  reviews       = lookupsIter.toList
                } yield (reviews)
              }
              future onComplete {
                case Success(reviews) =>
                  currentSender ! ListReviewResponse(
                    reviews       = Some(amalgamateReviews(Some(reviews),Some(response.reviews))),
                    businessName  = req.businessName,
                    status        = ServiceStatus.Success,
                    rating        = response.rating
                  )
                case Failure(error)   =>
                  log.info(s"Error while fetching results from Cassandra fowarding those from google $error")
                  currentSender ! ListReviewResponse(
                    reviews       = Some(amalgamateReviews(None,Some(response.reviews))),
                    businessName  = req.businessName,
                    status        = ServiceStatus.Success,
                    rating        = response.rating
                  )
              }
            case Failure(error)    =>
              log.info(s"Error while retrieving review data from Google [$error]")
              currentSender ! ListReviewResponse(
                reviews       = None,
                businessName  = req.businessName,
                status        = ServiceStatus.Error,
                rating        = None
              )
          }
        case Failure(error)    =>
          log.info(s"Error while retrieving place id + [$req] [$error]")
          currentSender ! ListReviewResponse(
            reviews       = None,
            businessName  = req.businessName,
            status        = ServiceStatus.Error,
            rating        = None
          )
      }
  }

  private def amalgamateReviews(
        A: Option[List[UserReviewDbEntry]],
        B: Option[List[Review]]
   ):List[Review] = {
    (A,B) match {
      case Tuple2(None, Some(y))    => y
      case Tuple2(None, None)       => List[Review]()
      case Tuple2(Some(x), None)    => 
        for {
            dbEntry <- x
            review = Review(
                authorName    = dbEntry.authorName,
                rating        = dbEntry.rating,
                textInfo      = dbEntry.textInfo,
                callback      = dbEntry.callback,
                insertionTime = dbEntry.insertionTime
            )
        } yield review
      case Tuple2(Some(x), Some(y)) => 
        val userReviewDbList = for {
            dbEntry <- x
            review = Review(
                authorName    = dbEntry.authorName,
                rating        = dbEntry.rating,
                textInfo      = dbEntry.textInfo,
                callback      = dbEntry.callback,
                insertionTime = dbEntry.insertionTime
            )
        } yield review
        userReviewDbList ::: y
    }
  }
}