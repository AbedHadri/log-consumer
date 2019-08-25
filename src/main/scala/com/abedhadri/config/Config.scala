package com.abedhadri.config

import com.typesafe.config.ConfigFactory
import pureconfig.generic.ProductHint
import pureconfig._
import pureconfig.generic.auto._

object Config {

  case class KafkaConfig(endpoint: String , groupId: String, offsetResetStrategy: String , topic: String)
  case class HttpConfig(interface: String , port:Int)
  case class AppConfig(http: HttpConfig)
  case class Configs(kafka: KafkaConfig ,app: AppConfig)

  implicit def hint[Configs] = ProductHint[Configs](
    fieldMapping = ConfigFieldMapping(CamelCase, CamelCase)
  )

  def loadConfigs: Configs = loadConfigOrThrow[Configs](ConfigFactory.load())
}
