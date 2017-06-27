package g8.akkahttp.eventsvc.data

import java.util.Date

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson._
import org.mongodb.scala.bson.codecs.Macros._

/**
  * Created by stephmajor on 6/26/17.
  */
case class Event(_id: ObjectId,
                 timestamp: Date,
                 latitude: Double,
                 longitude: Double,
                 state: EventSignatureState,
                 trigger: Trigger) {
}

object Event {

  val codecRegistry = fromRegistries(fromProviders(classOf[Event], classOf[EventSignatureState], classOf[Trigger]))
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
