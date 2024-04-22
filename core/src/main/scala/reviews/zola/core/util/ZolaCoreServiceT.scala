package reviews.zola.core.util

import akka.actor.ActorRefFactory

import reviews.zola._

import core.db.mysql.cache._

trait ZolaCoreServiceT {

  def actorRefFactory: ActorRefFactory

  AuthenticationDbCache.initialize(actorRefFactory.actorOf(
    AuthenticationDbCache.props
  ))
}