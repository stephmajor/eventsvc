package g8.akkahttp.eventsvc.test

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import g8.akkahttp.eventsvc.{EventService, Services, StatusService}

class StatusServiceTest extends ServiceTestBase with Services {
  import g8.akkahttp.eventsvc.data.Status
  import io.circe.generic.auto._

  private val _testSystem = ActorSystem()
  val _eventService = mock[EventService]
  val _statusService = new StatusService(_testSystem)

  "StatusService" when {
    "GET /v1/status" should {
      "return time" in {
        Get("/v1/status") ~> routes ~> check {
          status should be(StatusCodes.OK)
          responseAs[Status].uptime should include("milliseconds")
        }
      }
    }
  }
}
