package reviews.zola.api

import reviews.zola._

import core.util.{ AbstractApplicationDaemon, ZolaApplicationT }

class ApplicationDaemon extends AbstractApplicationDaemon {
  def application = new Application
}

object ServiceApplication extends App with ZolaApplicationT[ApplicationDaemon] {
  def createApplication = new ApplicationDaemon()
}