package g8.akkahttp.eventsvc

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait ResponseFactory {

  import Directives._
  import io.circe.generic.auto._

  def sendResponse[T](futureResult: Future[T])(implicit marshaller: T => ToResponseMarshallable): Route = {
    onComplete(futureResult) {
      case Success(result) => complete(result)
      case Failure(e) => complete(StatusCodes.InternalServerError, "request did not succeed")
    }
  }
}
