package g8.akkahttp.eventsvc.data.access

import scala.concurrent.Future

trait DataStoreComponent {

  def dataStore: DataStore

  trait DataStore {

    // EVENTS
    def createEvents(numToCreate: Int): Future[Boolean]
  }
}
