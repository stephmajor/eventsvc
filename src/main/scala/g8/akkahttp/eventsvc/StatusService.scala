package g8.akkahttp.eventsvc

import java.lang.management.ManagementFactory

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.{Directives, StandardRoute}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.concurrent.duration._

class StatusService(system: ActorSystem) extends StatusServiceTrait {

  override protected def log      = Logging(system, "service")
  override protected def executor = system.dispatcher
}

trait StatusServiceTrait extends BaseComponent {
  import Directives._
  import g8.akkahttp.eventsvc.data._
  import io.circe.generic.auto._

  def getStatus: StandardRoute = {
    log.info("/status executed")
    complete(Status(Duration(ManagementFactory.getRuntimeMXBean.getUptime, MILLISECONDS).toString()))
  }
}
