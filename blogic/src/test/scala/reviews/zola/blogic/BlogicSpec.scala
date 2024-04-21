package reviews.zola.blogic

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.Props
import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }

// import akka.testkit.TestProbe

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.BeforeAndAfterAll

import reviews.zola._

import core.util.ZolaEnum._

import util._

import Blogic._

class BlogicSpec extends AnyWordSpec with BeforeAndAfterAll {

  val testBlogic = ActorSystem("TestSystem").actorOf(Props(new Blogic{
  }))
//WORK ON TESTING THE ACTOR RESPONSE WITH THE NEW TESTKIT
  "The Blogic" when {
    "given an AddReviewRequest" should {
      "Process and give a valid response" in {
        val ourResponse = testBlogic ! AddReviewRequest(
          reviews = Review("My Test Transaction1")
        )
        // assert(ourResponse == AddReviewResponse(
        //   status   = ServiceStatus.Success,
        //   response = "Reviews appended successfully"
        // ))
      }
    }
  }
}