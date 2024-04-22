lazy val sharedSettings = Seq(
  organization     := "io.zola",
  version          := "0.1.0",
  scalaVersion     := "2.13.13",
  resolvers    ++= Seq(
    ("Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/").withAllowInsecureProtocol(true),
    ("Confluent Maven Repository" at "http://packages.confluent.io/maven/").withAllowInsecureProtocol(true)
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked"
  ) 
)

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
      "com.typesafe.akka"             %% "akka-slf4j"               % akkaVersion,
      "commons-daemon"                %  "commons-daemon"           % "1.1.0",
      "org.lz4"                       %  "lz4-java"                 % "1.4.1",
      "ch.qos.logback"                %  "logback-core"             % "1.2.1",
      "ch.qos.logback"                %  "logback-classic"          % "1.2.1",
      "com.github.jasync-sql"         %  "jasync-mysql"             % "2.2.+",
      "com.typesafe.akka"             %% "akka-testkit"             % akkaVersion      % Test,
      "org.scalatest"                 %% "scalatest"                % scalaTestVersion % Test,
      "org.scalatest"                 %% "scalatest-wordspec"       % scalaTestVersion % Test
    )
  )

lazy val zola = (project in file("."))
  .aggregate(core, blogic, api)

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