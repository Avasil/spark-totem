name := "spark-totem"

version := "1.0"
scalaVersion := "2.11.11"

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Maven repository" at "http://morphia.googlecode.com/svn/mavenrepo/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

val sparkVersion = "2.0.1"
val catsVersion = "1.0.0-RC2"
val catsEffectVersion = "0.6"

val catsDependencies = Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-free" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion
)

val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.diana-hep" %% "spark-root" % "0.1.14"
)

val other = Seq(
  "com.typesafe" % "config" % "1.3.1",
  "org.tukaani" % "xz" % "1.6"
)

libraryDependencies ++= catsDependencies ++ sparkDependencies ++ other
fork in run := true

assemblyJarName in assembly := "spark-totem.jar"
mainClass in assembly := Some("pgawrys.totem.Main")