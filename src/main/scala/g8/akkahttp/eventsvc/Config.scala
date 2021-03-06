package g8.akkahttp.eventsvc

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus
import net.ceedubs.ficus.readers.ArbitraryTypeReader

trait Config {
  import ArbitraryTypeReader._
  import Ficus._

  protected case class HttpConfig(interface: String, port: Int)

  protected val config: com.typesafe.config.Config = ConfigFactory.load()
  protected val httpConfig: HttpConfig = config.as[HttpConfig]("http")
}

object Config extends Config
