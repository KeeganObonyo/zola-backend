package io.zola.zolabackend.core.api

import io.zola.zola-backend._

import core.util.{ AbstractApplicationDaemon, ZolaApplicationT }

class ApplicationDaemon extends AbstractApplicationDaemon {
  def application = new Application
}

object ServiceApplication extends App with ZolaApplicationT[ApplicationDaemon] {
  def createApplication = new ApplicationDaemon
}