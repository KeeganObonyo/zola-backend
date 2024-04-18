package io.zola.zola-backend.blogic

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.Props
import akka.testkit.TestProbe

import io.iohk.atala._

import core.util.ZolaEnum._

import test._

import util._

class BlogicSpec extends TestServBlogic = system.actorOf(Props(new Blogic{
    override def append(reviews: Seq[Review]): String = "Reviews appended successfully"
  }))

  val transactions1 = Seq(Review("My Test Transaction1"), Review("My test Transaction2"))

  "The Blogic" must {
    "Process an AddReviewRequest and give a valid response" in {
      blogic ! AddReviewRequest(
        review = transactions1
      )
      expectMsg(AddBlockResponse(
        status   = ServiceStatus.Success,
        response = "Reviews appended successfully"
      ))
    }
  }
}
