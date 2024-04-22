package reviews.zola.core
package util

import reviews.zola._

import ZolaEnum.ZolaEnvironment

trait ZolaInstanceManagerT[T] {

  private var instance: Option[T] = None

  def getInstance: T = instance match {
    case Some(x) => x
    case None    => throw new Exception("Instance not set")
  }

  protected def setInstance(t: T): Unit =
    ZolaConfig.getEnvironment match {
      case ZolaEnvironment.Development =>
        instance = Some(t)
      case _                           =>
        instance match {
          case Some(_) => throw new Exception("Instance already set")
          case None    => instance = Some(t)
        }
    }
}