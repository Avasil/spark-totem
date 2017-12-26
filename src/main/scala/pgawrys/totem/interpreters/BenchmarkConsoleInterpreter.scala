package pgawrys.totem.interpreters

import cats.effect.IO
import cats.~>
import com.typesafe.config.{Config, ConfigFactory}
import pgawrys.totem.BenchmarkType.Filter
import pgawrys.totem.algebra.{BenchmarkAlg, DisplayResults, LoadSettings}
import pgawrys.totem.{BenchmarkType, Settings}

import scala.util.Try

object BenchmarkConsoleInterpreter extends (BenchmarkAlg ~> IO) {
  def apply[A](fa: BenchmarkAlg[A]): IO[A] = fa match {
    case LoadSettings(args) => IO(loadSettings(args))
    case DisplayResults(result) => IO(println(result.prettyPrint()))
  }

  private def loadSettings(args: Array[String]): Settings = {

    def getBenchmarkType(config: Config) = Try(config.getInt("type")) map BenchmarkType.apply getOrElse Filter

    def getPath(config: Config) = config.getString("path")

    val config = ConfigFactory.load().getConfig("benchmark")

    val (path, benchmarkType) = args.toList match {
      case benchType :: filePath :: _ =>
        (filePath, Try(BenchmarkType(benchType.toInt)) getOrElse getBenchmarkType(config))
      case benchType :: _ => (getPath(config), Try(BenchmarkType(benchType.toInt)) getOrElse getBenchmarkType(config))
      case _ => (getPath(config), getBenchmarkType(config))
    }

    val parquet: Boolean = Try(config.getBoolean("parquet")).getOrElse(false)
    val persist: Boolean = Try(config.getBoolean("persist")).getOrElse(false)
    val iterations: Int = Try(config.getInt("iterations")).getOrElse(5)
    val limit: Option[Int] = Try(config.getInt("limit")).toOption

    val settings = Settings(path, iterations, parquet, persist, benchmarkType, limit)
    println(settings.prettyString)
    settings
  }

}