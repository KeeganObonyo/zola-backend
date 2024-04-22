package reviews.zola.core.util

object ZolaEnum { 
  object ServiceStatus extends Enumeration {
    val Success, Failed = Value
  }
  object ZolaEnvironment extends Enumeration {
    val Development, Production = Value
  }
}