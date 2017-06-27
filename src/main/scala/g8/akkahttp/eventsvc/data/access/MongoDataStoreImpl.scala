package g8.akkahttp.eventsvc.data.access

import g8.akkahttp.eventsvc.data.Event
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

      val events = for (i <- 0 to numToCreate - 1) yield Event.generateRandom

      collection.insertMany(events).subscribe(new Observer[Completed] {
        override def onNext(res: Completed): Unit = {}
        override def onError(e: Throwable): Unit = { println(s"error occurred ${e.getMessage}"); p.failure(e) }
        override def onComplete(): Unit = { println("completed"); p.success(true) }
      })

      p.future
    }
  }
}

object MongoDataStoreConnection {

//  private val uri: String = "mongodb://engineering:SmartDriveRocks!@cluster0-shard-00-00-3v0jb.mongodb.net:27017,cluster0-shard-00-01-3v0jb.mongodb.net:27017,cluster0-shard-00-02-3v0jb.mongodb.net:27017/admin?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin"
//  System.setProperty("org.mongodb.async.type", "netty")

  // Introduce the standard UUID codec for compatibility with other drivers

  lazy val client: MongoClient = MongoClient()  // going to the local mongod daemon
}
