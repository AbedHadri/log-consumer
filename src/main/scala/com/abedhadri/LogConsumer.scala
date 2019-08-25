package com.abedhadri

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.{ActorMaterializer, Materializer}
import cats.implicits._
import com.abedhadri.config.Config
import com.abedhadri.model.LogEntry
import com.abedhadri.service.RestService
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object LogConsumer extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("log-consumer")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val configs = Config.loadConfigs

  Http()
    .bindAndHandle(
      new RestService().routes,
      configs.app.http.interface,
      configs.app.http.port
    ).map(_ => logger.info(s"Server started at port ${configs.app.http.port}"))

  val consumerSettings =
    ConsumerSettings(system, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers(configs.kafka.endpoint)
      .withGroupId(configs.kafka.groupId)
        .withWakeupTimeout(6.seconds)

  Consumer
    .plainSource(consumerSettings, Subscriptions.topics(configs.kafka.topic))
    .map(_.value())
    .via(parseLogs)
    .via(persistLogs)
    .runWith(Sink.foreach(msg => logger.info("message recieved : {}", msg)))
    .onComplete {
      case Success(_) =>
        logger.info("Done")
        system.terminate()
      case Failure(err) =>
        logger.error("An error occured : " + err.toString)
        system.terminate()
    }

  def parseLogs =
    Flow[Array[Byte]]
      .map(bytes => new String(bytes, StandardCharsets.UTF_8))
      .map { logRow =>
        val fields = logRow.split(" ")
        (fields.headOption, fields.drop(1).headOption, fields.drop(2).headOption, fields.drop(3).headOption).mapN(LogEntry)
      }.collect {
      case Some(logEntry) => logEntry
    }

  def persistLogs = Flow[LogEntry]
    .mapAsync(1)(Future.successful) // TODO: persist your data the way you like.

}
