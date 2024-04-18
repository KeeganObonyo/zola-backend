package io.zola.zola-backend.core.util

import com.typesafe.config.ConfigFactory

object ZolaConfig exteZolaConfigT 

private[util] trait ZolaConfigT {

val config = ConfigFactory.load
  config.checkValid(ConfigFactory.defaultReference)

  // API
  val apiInterface = config.getString("zola.web-service.api.interface")
  val apiPort      = config.getInt("zola.web-service.api.port")

  //Actor TimeOut
  val serviceTimeout    = ZolaUtil.parseFiniteDuration(config.getString("zola-backend.actor-timeout.service")).get
  val webRequestTimeout = ZolaUtil.parseFiniteDuration(config.getString("zola-backend.actor-timeout.web-request")).get
}
