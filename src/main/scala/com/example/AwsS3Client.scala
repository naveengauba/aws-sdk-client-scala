package com.example

import software.amazon.awssdk.core.waiters.WaiterResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{CreateBucketRequest, DeleteBucketRequest, HeadBucketRequest, S3Exception}
import software.amazon.awssdk.services.s3.waiters.S3Waiter

object AwsS3Client extends App {
  println("S3Client example..")

  val region = Region.US_EAST_1
  val s3Client: S3Client = S3Client.builder.region(region).build()

  private def listBuckets(s3Client: S3Client): Unit = {
    println("\nListing all buckets..")
    s3Client.listBuckets().buckets().forEach(b => println(b.name()))
  }

  private def createBucket(s3Client: S3Client, bucketName: String): Unit = {
    println(s"\nCreating bucket: ${bucketName}")
    val waiter: S3Waiter = s3Client.waiter()
    val bucketRequest: CreateBucketRequest = CreateBucketRequest.builder().bucket(bucketName).build()
    s3Client.createBucket(bucketRequest);

    val bucketRequestWait = HeadBucketRequest.builder().bucket(bucketName)
      .build();

    // Wait until the bucket is created and print out the response.
    val waiterResponse = waiter.waitUntilBucketExists(bucketRequestWait)
    waiterResponse.matched().response().isPresent
    println(s"Bucket ${bucketName} created")
  }

  private def deleteBucket(s3Client: S3Client, bucketName: String): Unit = {
    println(s"\nDeleting bucket: ${bucketName}")
    val deleteBucketRequest: DeleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build()
    s3Client.deleteBucket(deleteBucketRequest)
    println(s"Bucket ${bucketName} deleted")
  }

  val bucketName = "ng-test-bucket" + System.currentTimeMillis
  System.out.println(bucketName)

  try {
    createBucket(s3Client, bucketName)

    listBuckets(s3Client)

    deleteBucket(s3Client, bucketName)

    listBuckets(s3Client)
  } catch {
    case e: S3Exception => println("Exception from AWS")
    case _: Throwable => println("Got a Throwable exception")
  }
  finally {
    s3Client.close()
  }
}
