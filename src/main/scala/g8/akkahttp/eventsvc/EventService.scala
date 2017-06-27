package g8.akkahttp.eventsvc

import java.lang.management.ManagementFactory

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.{Directives, StandardRoute}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.concurrent.duration.{Duration, MILLISECONDS}

class EventService(system: ActorSystem) extends EventServiceTrait {

  override protected def log      = Logging(system, "service")
  override protected def executor = system.dispatcher
}

trait EventServiceTrait extends BaseComponent {
  import Directives._
  import g8.akkahttp.eventsvc.data._
  import io.circe.generic.auto._

  def getStatus: StandardRoute = {
    log.info("/event executed")
    complete(Status(Duration(ManagementFactory.getRuntimeMXBean.getUptime, MILLISECONDS).toString()))
  }

  def readEvent(eventId: String): StandardRoute = {
    log.info("/event/read executed")
    complete(s"Requesting an event by Id: $eventId")
  }


//    (get & path("read")) {
//      get {
//        log.info("/event/read executed")
//        complete("reached the event/read route")
//      }
//    }

//  path("read" / Segment) { eventId =>
//      get {
//        complete(s"Requesting an event by Id: $eventId")
//      }
//    } ~
//    (path("create") & post) {
//      entity(as[Int]) { numToCreate =>
//        complete(s"Creating $numToCreate random events")
//      }
//    }
}
