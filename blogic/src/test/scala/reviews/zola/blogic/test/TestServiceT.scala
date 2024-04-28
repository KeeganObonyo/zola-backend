package reviews.zola.blogic.test

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll

import reviews.zola._

import core.util.ZolaCoreServiceT

abstract class TestServiceT extends TestKit(ActorSystem("TestSystem"))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll  {

  override def beforeAll {
    Thread.sleep(2000)
  }

  override def afterAll {
    Thread.sleep(2000)
    TestKit.shutdownActorSystem(system)
  }
}
