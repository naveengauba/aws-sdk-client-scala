val scala3Version = "3.2.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "aws-sdk-client-scala",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies += "software.amazon.awssdk" % "dynamodb" % "2.18.4",
    libraryDependencies += "software.amazon.awssdk" % "s3" % "2.18.4",
    libraryDependencies += "software.amazon.awssdk" % "dynamodb-enhanced" % "2.18.4",
    libraryDependencies += "software.amazon.awssdk" % "kinesis" % "2.18.4"
  )
