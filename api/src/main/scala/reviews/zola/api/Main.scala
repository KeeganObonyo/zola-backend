package reviews.zola.api

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.io.IO
import akka.stream.Materializer

import akka.http.scaladsl._

import reviews.zola._

import core.util.{ ZolaConfig, ZolaLog, ApplicationLifecycle }

class Application extends ApplicationLifecycle with ZolaLog {

  private[this] var started: Boolean = false

  private val applicationName = "zola-backend-api"

  implicit val actorSystem    = ActorSystem(s"$applicationName-system")

  def start():Unit = {
    log.info(s"Starting $applicationName Service")

    if (!started) {
      implicit val Mat: Materializer = Materializer(actorSystem)
      Http().newServerAt(
        ZolaConfig.apiInterface,
        ZolaConfig.apiPort
      ).bind(
          new ApiServiceT {
          override def actorRefFactory = actorSystem
        }.route)
      started = true
    }
  }

  def stop():Unit = {
    log.info(s"Stopping $applicationName Service")

    if (started) {
      started = false
      actorSystem.terminate()
    }
  }

}