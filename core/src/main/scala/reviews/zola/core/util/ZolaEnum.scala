package reviews.zola.core.util

object ZolaEnum { 
  object ServiceStatus extends Enumeration {
    val Success, Failed, Error = Value
  }
  object ZolaEnvironment extends Enumeration {
    val Development, Production = Value
  }
}