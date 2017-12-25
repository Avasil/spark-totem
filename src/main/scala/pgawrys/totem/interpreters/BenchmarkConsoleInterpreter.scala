package pgawrys.totem.interpreters

import cats.effect.IO
import cats.~>
import com.typesafe.config.ConfigFactory
import pgawrys.totem.BenchmarkType.Filter
import pgawrys.totem.algebra.{BenchmarkAlg, DisplayResults, LoadSettings}
import pgawrys.totem.{BenchmarkType, Settings}

import scala.util.Try

object BenchmarkConsoleInterpreter extends (BenchmarkAlg ~> IO) {
  def apply[A](fa: BenchmarkAlg[A]): IO[A] = fa match {
    case LoadSettings => IO(loadSettings())
    case DisplayResults(result) => IO(println(result.prettyPrint()))
  }

  private def loadSettings(): Settings = {
    val config = ConfigFactory.load().getConfig("benchmark")

    val path = config.getString("path")
    val parquet: Boolean = Try(config.getBoolean("parquet")).getOrElse(false)
    val persist: Boolean = Try(config.getBoolean("persist")).getOrElse(false)
    val iterations: Int = Try(config.getInt("iterations")).getOrElse(5)
    val benchmarkType: BenchmarkType = Try(config.getInt("type")).map(BenchmarkType.apply).getOrElse(Filter)
    val limit: Option[Int] = Try(config.getInt("limit")).toOption


    Settings(path, iterations, parquet, persist, benchmarkType, limit)
  }

}