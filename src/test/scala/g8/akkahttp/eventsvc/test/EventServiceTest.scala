package g8.akkahttp.eventsvc.test

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import g8.akkahttp.eventsvc.{EventService, Services, StatusService}

class EventServiceTest extends ServiceTestBase with Services {
  import g8.akkahttp.eventsvc.data.Status
  import io.circe.generic.auto._

  private val _testSystem = ActorSystem()
  val _eventService: EventService = new EventService(_testSystem)
  val _statusService: StatusService = mock[StatusService]

  "EventService - root" when {
    "GET /v1/event" should {
      "return time" in {
        Get("/v1/event") ~> routes ~> check {
          status should be(StatusCodes.OK)
          responseAs[Status].uptime should include("milliseconds")
        }
      }
    }
  }

  "EventService - read" when {
    "GET /v1/event/read/myEventId" should {
      "return message" in {
        Get("/v1/event/read/myEventId") ~> routes ~> check {
          status should be(StatusCodes.OK)
          responseAs[String] shouldEqual "Requesting an event by Id: myEventId"
        }
      }
    }
  }

  //  "EventService" when {
//    "GET /event/read/myEventId" should {
//      "return a string" in {
//        Get("/event/read/myEventId") ~> routes ~> check {
//          status should be(StatusCodes.OK)
//          responseAs[String] should be("Requesting an event by Id: myEventId")
//        }
//      }
//    }
//  }
}
