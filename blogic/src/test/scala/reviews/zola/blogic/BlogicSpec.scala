package reviews.zola.blogic

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.{ ActorSystem, Props }
import akka.testkit.{ ImplicitSender, TestActors, TestKit }

// import akka.testkit.TestProbe
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll

import reviews.zola._

import core.util.ZolaEnum._

import util._

import Blogic._

class BlogicSpec extends TestKit(ActorSystem("BlogicSpec"))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val testBlogic = system.actorOf(Props(new Blogic{
  }))
  
  "The Blogic" when {
    "given an AddReviewRequest" should {
      "Process and give a valid response" in {
        val ourResponse = testBlogic ! AddReviewRequest(
          reviews = Review("My Test Transaction1")
        )
        expectMsg(AddReviewResponse(
          status   = ServiceStatus.Success,
          response = "Reviews appended successfully"
        ))
      }
    }
  }
}