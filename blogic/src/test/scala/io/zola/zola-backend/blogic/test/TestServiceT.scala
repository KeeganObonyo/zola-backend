package io.zola.zolabackend.core.blogic
package test

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }

import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import io.zola._

abstract class TestServiceT extends TestKit(ActorSystem("TestSystem"))
    with ImplicitSender
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll {

  override def beforeAll: Unit = {
    Thread.sleep(2000)
  }

  override def afterAll: Unit = {
    Thread.sleep(2000)
    TestKit.shutdownActorSystem(system)
  }
}
