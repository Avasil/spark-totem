package pgawrys.totem

import cats.data.EitherK
import cats.effect.IO
import cats.free.Free
import cats.~>
import pgawrys.totem.algebra.BenchmarkAlg.BenchmarkOps
import pgawrys.totem.algebra.SparkAlg.SparkOps
import pgawrys.totem.algebra.{BenchmarkAlg, SparkAlg}
import pgawrys.totem.interpreters.{BenchmarkConsoleInterpreter, SparkInterpreter}

object Main {
  def main(args: Array[String]): Unit = {

    type SparkBenchmark[A] = EitherK[BenchmarkAlg, SparkAlg, A]

    def program(implicit
                benchmarkOps: BenchmarkOps[SparkBenchmark],
                sparkOps: SparkOps[SparkBenchmark]): Free[SparkBenchmark, Unit] = {
      import benchmarkOps._
      import sparkOps._

      for {
        sparkSession <- initializeSpark()
        _ <- {
          implicit val spark = sparkSession
          for {
            settings <- loadSettings()
            df <- loadDataFrame(settings.path, settings.parquet, settings.limit)
            result <- runBenchmark(df, settings)
            _ <- displayResults(result)
          } yield ()
        }
      } yield ()
    }

    val interpreter: SparkBenchmark ~> IO = BenchmarkConsoleInterpreter or SparkInterpreter
    val ioProgram: IO[Unit] = program foldMap interpreter

    ioProgram.unsafeRunSync()
  }
}