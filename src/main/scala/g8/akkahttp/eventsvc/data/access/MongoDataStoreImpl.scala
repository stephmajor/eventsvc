package g8.akkahttp.eventsvc.data.access

import com.mongodb.ConnectionString
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import g8.akkahttp.eventsvc.Config
import g8.akkahttp.eventsvc.data.Event
import org.mongodb.scala.connection.{ClusterSettings, SslSettings}
import org.mongodb.scala.{Completed, MongoClient, MongoClientSettings, MongoCollection, Observer, ServerAddress}

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
  }
}

object MongoDataStoreConnection extends Config {

//  import collection.JavaConverters._

  private val uri: String = config.getString("mongoConnectionUri")

//  private val cStr = new ConnectionString(uri)
//  private val atlasConfig = MongoClientSettings.builder()
//    .clusterSettings(ClusterSettings.builder().hosts(cStr.getHosts.asScala.map(new ServerAddress(_)).asJava).build())
//    .credentialList(cStr.getCredentialList)
//    .sslSettings(SslSettings.builder().enabled(true).build())
//    .streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
//    .build()

  System.setProperty("org.mongodb.async.type", "netty")

  // Introduce the standard UUID codec for compatibility with other drivers

  lazy val client: MongoClient = MongoClient(uri)
}
