name := "spark-totem"

version := "1.0"
scalaVersion := "2.11.11"

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Maven repository" at "http://morphia.googlecode.com/svn/mavenrepo/"
)

val sparkVersion = "2.2.0"
val catsVersion = "1.0.0-RC2"

val catsDependencies = Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-free" % catsVersion
)

val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.diana-hep" %% "spark-root" % "0.1.14"
)

val other = Seq(
  "com.typesafe" % "config" % "1.3.1",
  "org.tukaani" % "xz" % "1.6",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7"
)

libraryDependencies ++= catsDependencies ++ sparkDependencies ++ other