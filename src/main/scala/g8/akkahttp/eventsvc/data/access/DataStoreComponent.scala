package g8.akkahttp.eventsvc.data.access

import org.mongodb.scala.bson.BsonDocument

import scala.concurrent.Future

trait DataStoreComponent {

  def dataStore: DataStore

  trait DataStore {

    // EVENTS
    def createEvents(numToCreate: Int): Future[Boolean]
    def getEventsByType(triggerType: Int): Future[List[BsonDocument]]
  }
}
