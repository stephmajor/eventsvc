package g8.akkahttp.eventsvc.data.access

import g8.akkahttp.eventsvc.Config
import g8.akkahttp.eventsvc.data.Event
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, MongoClient, MongoCollection, Observer}

import scala.concurrent.{Future, Promise}

trait MongoDataStoreImpl extends DataStoreComponent  {

  private val _dataStoreImpl = new DataStoreImpl
  def dataStore: DataStore = _dataStoreImpl

  class DataStoreImpl extends DataStore {

    // EVENTS

    override def createEvents(numToCreate: Int): Future[Boolean] = {
      val p = Promise[Boolean]()
      val database = MongoDataStoreConnection.client.getDatabase("sdsi").withCodecRegistry(Event.codecRegistry)
      val collection: MongoCollection[Event] = database.getCollection("events")

      val events = for (i <- 0 until numToCreate) yield Event.generateRandom

      collection.insertMany(events).subscribe(new Observer[Completed] {
        override def onNext(res: Completed): Unit = {}
        override def onError(e: Throwable): Unit = { println(s"error occurred ${e.getMessage}"); p.failure(e) }
        override def onComplete(): Unit = { println("completed"); p.success(true) }
      })

      p.future
    }

    override def getEventsByType(triggerType: Int): Future[List[BsonDocument]] = {
      val p = Promise[List[BsonDocument]]
      val database = MongoDataStoreConnection.client.getDatabase("sdsi")
      val collection: MongoCollection[BsonDocument] = database.getCollection("events")

      collection.find(Filters.`eq`("trigger.triggerType", triggerType)).collect().subscribe(
        (results: Seq[BsonDocument]) => p.success(results.toList),
        (error: Throwable) => p.failure(error)
      )

      p.future
    }
  }
}

object MongoDataStoreConnection extends Config {

  private val uri: String = config.getString("mongoConnectionUri")
  System.setProperty("org.mongodb.async.type", "netty")

  // TODO: Introduce the standard UUID codec for compatibility with other drivers

  lazy val client: MongoClient = MongoClient(uri)
}
