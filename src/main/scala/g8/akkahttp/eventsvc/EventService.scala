package g8.akkahttp.eventsvc

import java.lang.management.ManagementFactory

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import g8.akkahttp.eventsvc.data.access.{DataStoreComponent, MongoDataStoreImpl}

import scala.concurrent.duration.{Duration, MILLISECONDS}

class EventService(system: ActorSystem) extends EventServiceTrait with MongoDataStoreImpl {

  override protected def log      = Logging(system, "service")
  override protected def executor = system.dispatcher
}

trait EventServiceTrait extends BaseComponent with DataStoreComponent with ResponseFactory {
  import Directives._
  import g8.akkahttp.eventsvc.data._
  import io.circe.generic.auto._

  def getStatus: Route = {
    log.info("/event executing")
    complete(Status(Duration(ManagementFactory.getRuntimeMXBean.getUptime, MILLISECONDS).toString()))
  }

  def readEvent(eventId: String): Route = {
    log.info("/event/read executing")
    complete(s"Requesting an event by Id: $eventId")
  }

  def createEvents(ecr: EventCreationRequest): Route = {
    log.info("/event/create executing")
    sendResponse(dataStore.createEvents(ecr.howMany))
  }
}
