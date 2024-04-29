lazy val sharedSettings = Seq(
  organization     := "io.zola",
  version          := "0.1.0",
  scalaVersion     := "2.12.19",
  resolvers    ++= Seq(
    ("Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/").withAllowInsecureProtocol(true),
    ("Confluent Maven Repository" at "http://packages.confluent.io/maven/").withAllowInsecureProtocol(true)
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked"
  ),
  assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
  }
)

lazy val zola = (project in file("."))
  .aggregate(core, blogic, api)

val akkaVersion      = "2.8.5"
val akkaHttpVersion  = "10.2.10"
val scalaTestVersion = "3.2.18"

lazy val core = (project in file("core")).
  settings(
    sharedSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka"             %% "akka-actor"               % akkaVersion,
      "com.typesafe.akka"             %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka"             %% "akka-http-spray-json"     % akkaHttpVersion,
      // "com.typesafe.akka"             %% "akka-actor-testkit-typed" % "2.8.0"         % Test,
      "com.typesafe.akka"             %% "akka-slf4j"               % akkaVersion,
      "org.slf4j"                     %  "slf4j-simple"             % "2.0.9",
      "org.scalikejdbc"               %% "scalikejdbc-async"        % "0.14.0",
      // "org.scalikejdbc"               %% "scalikejdbc-test"         % "4.2.1"         % "test",
      // "org.scalikejdbc"               %% "scalikejdbc-config"       % "4.2.1",
      "com.github.jasync-sql"         %  "jasync-mysql"             % "2.2.+",
      "com.outworkers"                %% "phantom-dsl"              % "2.59.0",
      "com.outworkers"                %% "phantom-streams"          % "2.42.0",
      // "joda-time"                     %  "joda-time"                % "2.10.2", 
      // "org.joda"                      %  "joda-convert"             % "2.2.1",
      "commons-daemon"                %  "commons-daemon"           % "1.3.4",
      "org.lz4"                       %  "lz4-java"                 % "1.4.1",
      "ch.qos.logback"                %  "logback-core"             % "1.2.1",
      "ch.qos.logback"                %  "logback-classic"          % "1.2.1",
      "com.typesafe.akka"             %% "akka-testkit"             % akkaVersion      % Test,
      "org.scalatest"                 %% "scalatest"                % scalaTestVersion % Test,
      "org.scalatest"                 %% "scalatest-wordspec"       % scalaTestVersion % Test,
      // "org.scala-lang"                %  "scala-reflect"            % "2.12.19",
      "mysql"                         %  "mysql-connector-java"     % "8.0.+"
    ),
  )

lazy val blogic = (project in file("blogic")).
  settings(
    sharedSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-testkit"             % akkaVersion      % Test,
      "org.scalatest"     %% "scalatest"                % scalaTestVersion % Test,
      "org.scalatest"     %% "scalatest-wordspec"       % scalaTestVersion % Test
    )
  ).dependsOn(core)

lazy val api = (project in file("api")).
  settings(
    sharedSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-testkit"             % akkaVersion      % Test,
      "org.scalatest"     %% "scalatest"                % scalaTestVersion % Test,
      "org.scalatest"     %% "scalatest-wordspec"       % scalaTestVersion % Test
    )
  ).dependsOn(core, blogic)