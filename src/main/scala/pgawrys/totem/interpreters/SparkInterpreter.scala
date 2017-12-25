package pgawrys.totem.interpreters

import cats.effect.IO
import cats.~>
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.dianahep.sparkroot._
import pgawrys.totem.BenchmarkType.{Filter, HeavyComputation, Lorentz}
import pgawrys.totem.algebra.{InitializeSpark, LoadDataFrame, RunBenchmark, SparkAlg}
import pgawrys.totem.{Result, Settings}

object SparkInterpreter extends (SparkAlg ~> IO) {
  def apply[A](fa: SparkAlg[A]): IO[A] = fa match {
    case InitializeSpark => initializeSpark()
    case LoadDataFrame(path, parquet, limit, spark) => loadDataFrame(path, parquet, limit)(spark)
    case RunBenchmark(df, settings, spark) => runBenchmark(df, settings)(spark)
  }

  private def initializeSpark(): IO[SparkSession] = IO {
    val spark: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    spark
  }

  private def loadDataFrame(path: String, parquet: Boolean, limit: Option[Int])
                           (implicit spark: SparkSession): IO[DataFrame] = IO {
    println(s"Attempting to load dataframe from $path")

    val df =
      if (parquet) spark.sqlContext.read.parquet(path)
      else spark.sqlContext.read.root(path)

    println(s"Loaded DataFrame has ${df.count()} events.")

    limit filter (_ > 0) map df.limit getOrElse df
  }

  private def runBenchmark(df: DataFrame, settings: Settings)(implicit spark: SparkSession): IO[Result] = IO {
    import settings._
    import spark.implicits._
    val Dx_rp_3_m = -9.656e-02

    val dataframes: List[DataFrame] = List.fill(iterations) {
      benchmarkType match {
        case Filter =>
          df
            .filter($"track_rp_3_.valid" === true)
            .select($"track_rp_3_.x" / 1000.0 / Dx_rp_3_m as "xi_rp_3")
        case HeavyComputation => df
        case Lorentz => df
      }
    }
    val results = dataframes.map { dataframe =>
      if (persist)
        dataframe.persist()

      timed {
        println(s"Rows left: ${dataframe.count()}")
      }
    }

    Result(results)
  }

  private def timed[A](computation: => A): Double = {
    val start = System.nanoTime()
    computation
    (System.nanoTime() - start) / 1000000000.0
  }
}