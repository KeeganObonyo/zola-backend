package reviews.zola.core.util

import akka.actor.ActorRefFactory

trait ZolaCoreServiceT {

  def actorRefFactory: ActorRefFactory
}