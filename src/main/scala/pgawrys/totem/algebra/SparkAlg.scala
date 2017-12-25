package pgawrys.totem.algebra

import cats.InjectK
import cats.free.Free
import org.apache.spark.sql.{DataFrame, SparkSession}
import pgawrys.totem.{Result, Settings}

sealed trait SparkAlg[A]

case object InitializeSpark extends SparkAlg[SparkSession]

final case class LoadDataFrame(path: String, parquet: Boolean, limit: Option[Int], spark: SparkSession) extends SparkAlg[DataFrame]

final case class RunBenchmark(df: DataFrame, settings: Settings, spark: SparkSession) extends SparkAlg[Result]

object SparkAlg {

  class SparkOps[F[_]](implicit I: InjectK[SparkAlg, F]) {
    def initializeSpark(): Free[F, SparkSession] =
      Free.inject[SparkAlg, F](InitializeSpark)

    def runBenchmark(df: DataFrame, settings: Settings)(implicit spark: SparkSession): Free[F, Result] =
      Free.inject[SparkAlg, F](RunBenchmark(df, settings, spark))

    def loadDataFrame(path: String, parquet: Boolean, limit: Option[Int])(implicit spark: SparkSession): Free[F, DataFrame] =
      Free.inject[SparkAlg, F](LoadDataFrame(path, parquet, limit, spark))
  }

  object SparkOps {
    implicit def sparkConv[F[_]](implicit I: InjectK[SparkAlg, F]): SparkOps[F] = new SparkOps[F]()
  }
}