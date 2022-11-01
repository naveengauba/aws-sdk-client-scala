package com.example

import software.amazon.awssdk.core.waiters.WaiterResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.model.{AttributeDefinition, CreateTableRequest, CreateTableResponse, DeleteTableRequest, DescribeTableRequest, DescribeTableResponse, DynamoDbException, KeySchemaElement, KeyType, ProvisionedThroughput, ScalarAttributeType}
import software.amazon.awssdk.services.dynamodb.{DynamoDbAsyncClient, DynamoDbClient}
import software.amazon.awssdk.services.dynamodb.waiters.{DynamoDbAsyncWaiter, DynamoDbWaiter}
import software.amazon.awssdk.services.s3.waiters.S3Waiter

import java.util.concurrent.CompletableFuture
import scala.jdk.CollectionConverters.*

object DynamoDbClientExample extends App {

  println("DynamoDbClient example..")

  val dynamoDbClient: DynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build()

  val asyncDynamoDbClient: DynamoDbAsyncClient = DynamoDbAsyncClient.builder().region(Region.US_EAST_1).build()

  private def listTables(ddb: DynamoDbClient): Unit = {
    println("\nListing all tables..")
    ddb.listTables().tableNames().forEach(println)
  }

  private def deleteTable(asyncDynamoDbClient: DynamoDbAsyncClient, tableName: String): Unit = {
    val asyncWaiter: DynamoDbAsyncWaiter = asyncDynamoDbClient.waiter()

    asyncDynamoDbClient.deleteTable(DeleteTableRequest.builder().tableName(tableName).build())

    val describeTableRequest: DescribeTableRequest = DescribeTableRequest.builder().tableName(tableName).build()

    val asyncWaiterResponse: CompletableFuture[WaiterResponse[DescribeTableResponse]] = asyncWaiter.waitUntilTableNotExists(describeTableRequest)
    asyncWaiterResponse.whenComplete((response, ex) => {
      if (ex == null) {
        println(s"Table ${tableName} deleted")
      }
    }).join()
  }

  private def createTable(asyncDynamoDbClient: DynamoDbAsyncClient, tableName: String, key: String): Unit = {
    val asyncWaiter: DynamoDbAsyncWaiter = asyncDynamoDbClient.waiter()
    val createTableRequest: CreateTableRequest = CreateTableRequest.builder().attributeDefinitions(
      AttributeDefinition.builder().attributeName(key).attributeType(ScalarAttributeType.S).build())
      .keySchema(KeySchemaElement.builder().attributeName(key).keyType(KeyType.HASH).build())
      .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(10).writeCapacityUnits(10).build())
      .tableName(tableName).build()

    val createTableResponse: CreateTableResponse = dynamoDbClient.createTable(createTableRequest)
    val describeTableRequest: DescribeTableRequest = DescribeTableRequest.builder().tableName(tableName).build()

    val asyncWaiterResponse: CompletableFuture[WaiterResponse[DescribeTableResponse]] = asyncWaiter.waitUntilTableExists(describeTableRequest)
    asyncWaiterResponse.whenComplete((response, ex) => {
      if (ex == null) {

        println(s"Table ${tableName} created")
      }
    }).join()
  }

  val tableName = "ng-test-table" + System.currentTimeMillis
  System.out.println(tableName)

  try {
    listTables(dynamoDbClient)

    createTable(asyncDynamoDbClient, tableName, "id")

    deleteTable(asyncDynamoDbClient, tableName)
  } catch {
    case e: DynamoDbException => println("Exception from AWS")
    case _: Throwable => println("Got a Throwable exception")
  }
  finally {
    dynamoDbClient.close()
  }
}
