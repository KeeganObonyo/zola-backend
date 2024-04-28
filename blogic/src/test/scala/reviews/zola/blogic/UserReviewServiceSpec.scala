package reviews.zola.blogic

import scala.concurrent.duration._
import scala.language.postfixOps

import org.joda.time.DateTime

import akka.actor.Props
import akka.testkit.TestProbe

import reviews.zola._

import core.util.ZolaEnum._

import blogic.test._

import core.db.cassandra.CassandraDbQueryResult
import core.db.cassandra.service.UserReviewCassandraDbService._

import gateway.GooglePlacesApiGateway._

import UserReviewService._

class UserReviewServiceSpec extends TestServiceT {

  val userReviewCassandraDbServiceProbe = TestProbe()
  val gatewayProbe                      = TestProbe()

  val testUserReviewService = system.actorOf(Props(new UserReviewService{
    override def createGateway                      = gatewayProbe.ref
    override def createUserReviewCassandraDbService = userReviewCassandraDbServiceProbe.ref
    override def retrieveTxId: String = "SomeTxId"
  }))
  
  "given an AddReviewRequest" should {
    "Process and give a valid response" in {
      val ourResponse = testUserReviewService ! AddReviewRequest(
          userId        = 1,
          rating        = 5,
          businessName  = "Zola",
          callback      = Some("+254705417514"),
          textInfo      = Some("Good Stuff"),
          authorName    = "Comodo Dragon"
      )

      val userReviewCreateDbQuery = userReviewCassandraDbServiceProbe.expectMsgType[UserReviewCreateDbQuery]
      val insertionTime           = userReviewCreateDbQuery.insertionTime
      userReviewCreateDbQuery should be (UserReviewCreateDbQuery(
        transactionId = "SomeTxId",
        userId        = 1,
        insertionTime = insertionTime,
        rating        = 5,
        businessName  = "Zola",
        callback      = Some("+254705417514"),
        textInfo      = Some("Good Stuff"),
        authorName    = "Comodo Dragon"
      ))
      userReviewCassandraDbServiceProbe.reply(new CassandraDbQueryResult(true))

      expectMsg(AddReviewResponse(
        status      = ServiceStatus.Success,
        description = "Review appended successfully"
      ))
    }
  }
  "given a ListReviewRequest" should {
    "Process and give a valid response" in {
      val ourResponse = testUserReviewService ! ListReviewRequest(
          userId       = 1,
          businessName = "Zola"
      )
      val mockTime = DateTime.now()
      gatewayProbe.expectMsg(FindPlaceIdRequest("Zola"))
      gatewayProbe.reply(FindPlaceIdResponse(Some("someplaceId"), ServiceStatus.Success))
      gatewayProbe.expectMsg(FindPlaceDetailRequest("someplaceId"))
      gatewayProbe.reply(FindPlaceDetailResponse(
        reviews =  List(Review(
          insertionTime = mockTime,
          rating        = 3,
          callback   = Some("+254705417514"),
          textInfo   = Some("Good Stuff"),
          authorName = "A Dragon"
        )),
        status  = ServiceStatus.Success,
        rating  = Some(3)
      ))
      val userReviewFetchDbQuery = userReviewCassandraDbServiceProbe.expectMsg(UserReviewFetchDbQuery(
          1, None, 1000, None, None, None
      ))
      userReviewCassandraDbServiceProbe.reply(Iterator(UserReviewDbEntry(
        transactionId = "SomeTxId",
        userId        = 1,
        insertionTime = mockTime,
        rating        = 5,
        businessName  = "Zola",
        callback      = Some("+254705417514"),
        textInfo      = Some("Good Stuff"),
        authorName    = "Comodo Dragon"
      )))

      expectMsg(ListReviewResponse(
        reviews      = Some(List(Review(
          insertionTime = mockTime,
          rating        = 5,
          callback   = Some("+254705417514"),
          textInfo   = Some("Good Stuff"),
          authorName = "Comodo Dragon"
        ),
        Review(
          insertionTime = mockTime,
          rating        = 3,
          callback   = Some("+254705417514"),
          textInfo   = Some("Good Stuff"),
          authorName = "A Dragon"
        ))),
        businessName = "Zola",
        status       = ServiceStatus.Success,
        rating       = Some(3)
      ))
    }
  }
}