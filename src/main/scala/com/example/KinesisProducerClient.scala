package com.example

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisClient
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest
import software.amazon.awssdk.services.kinesis.model.KinesisException
import software.amazon.awssdk.services.s3.model.S3Exception

object KinesisProducerClient extends App {

  println("KinesisClient example")

  def createStream(kinesisClient: KinesisClient, streamName: String): Unit = {
    println(s"\nCreating stream: ${streamName}")
    val streamReq = CreateStreamRequest.builder.streamName(streamName).shardCount(1).build
    kinesisClient.createStream(streamReq)
    println(s"Stream ${streamName} created")
  }

  def listStream(kinesisClient: KinesisClient): Unit = {
    kinesisClient.listStreams().streamNames().forEach(s => println(s))
  }

  val kinesisClient: KinesisClient = KinesisClient.builder().region(Region.US_EAST_1).build()
  val streamName = "ng-test-stream" + System.currentTimeMillis

  try {
    listStream(kinesisClient)
    //createStream(kinesisClient, streamName)
  } catch {
    case e: KinesisException => println("Exception from AWS")
  } finally {
    kinesisClient.close()
  }
}
