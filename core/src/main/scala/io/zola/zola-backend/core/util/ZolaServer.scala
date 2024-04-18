package io.zola.zola-backend.core.util

import org.apache.commons.daemon.{ Daemon, DaemonContext }

trait ApplicationLifecycle {
  def start(): Unit
  def stop(): Unit
}

abstract class AbstractApplicationDaemon extends Daemon {
  def application: ApplicationLifecycle

  def init(daemonContext: DaemonContext):Unit = {}

  def start() = application.start()

  def stop() = application.stop()

  def destroy() = application.stop()
}

trait ZolaApplicationT[T <: AbstractApplicationDaemon] {
  
  val application = createApplication()
  def createApplication() : T

  private[this] var cleanupAlreadyRun: Boolean = false

  def cleanup():Unit = {
    val previouslyRun = cleanupAlreadyRun
    cleanupAlreadyRun = true
    if (!previouslyRun) application.stop()
  }

  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
    def run():Unit = {
      cleanup()
    }
  }))

  application.start()
}