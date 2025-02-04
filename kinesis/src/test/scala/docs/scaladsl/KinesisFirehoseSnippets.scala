/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package docs.scaladsl

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.kinesisfirehose.KinesisFirehoseFlowSettings
import akka.stream.alpakka.kinesisfirehose.scaladsl.{KinesisFirehoseFlow, KinesisFirehoseSink}
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.{ActorMaterializer, Materializer}
import software.amazon.awssdk.services.firehose.FirehoseAsyncClient
import software.amazon.awssdk.services.firehose.model.{PutRecordBatchResponseEntry, Record}

object KinesisFirehoseSnippets {

  //#init-client
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()

  implicit val amazonKinesisFirehoseAsync: software.amazon.awssdk.services.firehose.FirehoseAsyncClient =
    FirehoseAsyncClient.create()

  system.registerOnTermination(amazonKinesisFirehoseAsync.close())
  //#init-client

  //#flow-settings
  val flowSettings = KinesisFirehoseFlowSettings
    .create()
    .withParallelism(1)
    .withMaxBatchSize(500)
    .withMaxRecordsPerSecond(5000)
    .withMaxBytesPerSecond(4000000)

  val defaultFlowSettings = KinesisFirehoseFlowSettings.Defaults
  //#flow-settings

  //#flow-sink
  val flow1: Flow[Record, PutRecordBatchResponseEntry, NotUsed] = KinesisFirehoseFlow("myStreamName")

  val flow2: Flow[Record, PutRecordBatchResponseEntry, NotUsed] = KinesisFirehoseFlow("myStreamName", flowSettings)

  val sink1: Sink[Record, NotUsed] = KinesisFirehoseSink("myStreamName")
  val sink2: Sink[Record, NotUsed] = KinesisFirehoseSink("myStreamName", flowSettings)
  //#flow-sink

  //#error-handling
  val flowWithErrors: Flow[Record, PutRecordBatchResponseEntry, NotUsed] = KinesisFirehoseFlow("streamName")
    .map { response =>
      if (response.errorCode() != null) {
        throw new RuntimeException(response.errorCode())
      }
      response
    }
  //#error-handling

}
