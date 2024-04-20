package io.zola.zolabackend.core.api

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.io.IO
import akka.stream.ActorMaterializer

import io.zola.zolabackend._

import core.util.{ ZolaConfig, ZolaLog, ApplicationLifecycle }

class Application extends ApplicationLifecycle with ZolaBackendLog {

  private[this] var started: Boolean = false

  private val applicationName = "zola-backend-api"

  implicit val actorSystem    = ActorSystem(s"$applicationName-system")

  def start():Unit = {
    log.info(s"Starting $applicationName Service")

    if (!started) {

      implicit val materializer = ActorMaterializer()
      Http().bindAndHandle(
        new ApiServiceT {
          override def actorRefFactory = actorSystem
        }.route,
        ZolaBackendConfig.apiInterface,
        ZolaBackendConfig.apiPort
      )
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
