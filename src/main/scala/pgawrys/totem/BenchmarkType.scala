package pgawrys.totem

import org.apache.spark.sql.{Column, DataFrame, SparkSession}

sealed trait BenchmarkType {
  def run(df: DataFrame)(implicit spark: SparkSession): DataFrame
}

object BenchmarkType {

  def apply(i: Int): BenchmarkType = i match {
    case 2 => Lorentz
    case 3 => HeavyComputation
    case _ => Filter
  }

  case object Filter extends BenchmarkType {
    def run(df: DataFrame)(implicit spark: SparkSession): DataFrame = {
      import spark.implicits._
      val Dx_rp_3_m = -9.656e-02

      df
        .filter($"track_rp_3_.valid" === true)
        .select($"track_rp_3_.x" / 1000.0 / Dx_rp_3_m as "xi_rp_3")
    }
  }

  case object HeavyComputation extends BenchmarkType {
    def run(df: DataFrame)(implicit spark: SparkSession): DataFrame = {
      import org.apache.spark.sql.functions._
      import spark.implicits._

      def calcA(col: Column): Column = cos(pow(col, 2))
      def calcB(col: Column): Column = atan(sin(pow(col, 2)))
      def calcC(col: Column): Column = log(pow(sin(pow(col, 2)), 2) - 2)
      def calcD(col: Column): Column = factorial(pow(col, 4) % 1000)

      val colA = $"trigger_data_.run_num"
      val colB = $"track_rp_125_.x"
      val colC = $"track_rp_21_.thx"
      val colD = $"event_info_.daq_event_number"

      df.filter(sqrt((calcA(colA) - calcB(colB) + calcD(colD)) / calcC(colC)) > 0.0)
    }
  }

  case object Lorentz extends BenchmarkType {
    def run(df: DataFrame)(implicit spark: SparkSession): DataFrame = {
      import spark.implicits._
      val Dx_rp_3_m = -9.656e-02

      df
        .filter($"track_rp_3_.valid" === true)
        .select($"track_rp_3_.x" / 1000.0 / Dx_rp_3_m as "xi_rp_3")
    }
  }

}