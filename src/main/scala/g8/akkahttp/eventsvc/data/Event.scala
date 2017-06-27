package g8.akkahttp.eventsvc.data

import java.util.{Calendar, Date, UUID}

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

import scala.util.Random

case class Event(_id: ObjectId,
                 timestamp: Date,
                 latitude: Double,
                 longitude: Double,
                 state: EventSignatureState,
                 trigger: Trigger)

object Event {
  def apply(timestamp: Date,
            latitude: Double,
            longitude: Double,
            state: EventSignatureState,
            trigger: Trigger): Event = new Event(new ObjectId(), timestamp, latitude, longitude, state, trigger)

  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[Event], classOf[EventSignatureState], classOf[Trigger]), DEFAULT_CODEC_REGISTRY)

  def generateRandom: Event = {
    val tstamp = Calendar.getInstance().getTime
    val rand = new Random(tstamp.hashCode())

    val state = EventSignatureState(UUID.randomUUID().toString, rand.nextString(rand.nextInt(10)), "some description")

    val lat = rand.nextDouble() * 360.0 - 180.0
    val lon = rand.nextDouble() * 360.0 - 180.0

    val trigger = Trigger(rand.nextInt(15), tstamp, lat, lon, rand.nextDouble(), rand.nextDouble(), rand.nextInt(1000), rand.nextInt(1000))

    Event(tstamp, lat, lon, state, trigger)
  }
}

case class EventSignatureState(stateId: String,
                               name: String,
                               description: String)

case class Trigger(triggerType: Int,
                   timestamp: Date,
                   latitude: Double,
                   longitude: Double,
                   intensity: Double,
                   thresholdLevel: Double,
                   timeOffset: Long,
                   timeout: Long)

// REST Request objects

case class EventCreationRequest(howMany: Int)
