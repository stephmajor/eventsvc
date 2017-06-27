package g8.akkahttp.eventsvc

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer

import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import g8.akkahttp.eventsvc.data._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

trait BaseComponent extends Config {
  protected implicit def log: LoggingAdapter
  protected implicit def executor: ExecutionContext
}

object Main extends App with Config with Services {
  override protected def log = Logging(system, "service")
  override protected def executor: ExecutionContextExecutor = system.dispatcher

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  override val _statusService = new StatusService(system)
  override val _eventService  = new EventService(system)

  Http().bindAndHandle(routes, httpConfig.interface, httpConfig.port)
}

trait Services extends BaseComponent {
  import Directives._
  import io.circe.generic.auto._

  protected val _statusService: StatusService
  protected val _eventService: EventService

  private val apiVersion = "v1"

  val routes: Route = pathPrefix(apiVersion) {
    (path("status") & get) {
      _statusService.getStatus
    } ~
    pathPrefix("event") {
      get {
        pathEndOrSingleSlash {
          _eventService.getStatus
        } ~
        path("read" / Segment) { eventId =>
          _eventService.readEvent(eventId)
        }
      } ~
      post {
        path("create") {
          decodeRequest {
            entity(as[EventCreationRequest]) { ecr =>
              _eventService.createEvents(ecr)
            }
          }
        }
      }
    }
  }
}
