package pgawrys.totem.interpreters

import cats.effect.IO
import cats.~>
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.dianahep.sparkroot._
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

    (0 to 1) foreach { _ =>
      println (timed (println(s"(Warmup) Rows left: ${benchmarkType.run(df).count()}")))
    }

    val results =
      for (_ <- 0 until iterations)
        yield {
          if (persist)
            df.persist()
          val res = timed {
            println(s"Rows left: ${benchmarkType.run(df).count()}")
          }
          println(res)
          res
        }

    Result(results.drop(2).toList)
  }

  private def timed[A](computation: => A): Double = {
    val start = System.nanoTime()
    computation
    (System.nanoTime() - start) / 1000000000.0
  }
}