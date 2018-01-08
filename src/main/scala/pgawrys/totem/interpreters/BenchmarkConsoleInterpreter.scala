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

    val config = ConfigFactory.load().getConfig("benchmark")

    val benchmarkType = Try(BenchmarkType(args(0).toInt)) getOrElse getBenchmarkType(config)
    val path = Try(args(1)) getOrElse config.getString("path")
    val iterations = Try(args(2).toInt) getOrElse Try(config.getInt("iterations")).getOrElse(5)

    val parquet: Boolean = Try(args(3).toInt) map(_ == 1) getOrElse Try(config.getBoolean("parquet")).getOrElse(false)
    val persist: Boolean = Try(args(4).toInt) map(_ == 1) getOrElse Try(config.getBoolean("persist")).getOrElse(false)
    val limit: Option[Int] = (Try(args(5).toInt) orElse Try(config.getInt("limit"))).toOption

    val settings = Settings(path, iterations, parquet, persist, benchmarkType, limit)
    println(settings.prettyString)
    settings
  }

}