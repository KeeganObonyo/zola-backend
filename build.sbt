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
      "com.typesafe.akka"             %% "akka-slf4j"               % akkaVersion,
      "commons-daemon"                %  "commons-daemon"           % "1.3.4",
      "org.slf4j"                       %  "slf4j-simple"                 % "2.0.9",
      "org.scalikejdbc"               %% "scalikejdbc-async"        % "0.19.+",
      "org.scalikejdbc"               %% "scalikejdbc-test"         % "4.2.1"   % "test",
      "org.scalikejdbc"               %% "scalikejdbc-config"       % "4.2.1",
      "com.github.jasync-sql"         %  "jasync-mysql"             % "2.2.+",
      "joda-time"                     %  "joda-time"                % "2.10.2", 
      "org.joda"                      %  "joda-convert"             % "2.2.1",
      "com.typesafe.akka"             %% "akka-testkit"             % akkaVersion      % Test,
      "org.scalatest"                 %% "scalatest"                % scalaTestVersion % Test,
      "org.scalatest"                 %% "scalatest-wordspec"       % scalaTestVersion % Test
    )
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