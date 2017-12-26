package pgawrys.totem.algebra

import cats.InjectK
import cats.free.Free
import pgawrys.totem._

sealed trait BenchmarkAlg[A]

final case class LoadSettings(args: Array[String]) extends BenchmarkAlg[Settings]

final case class DisplayResults(result: Result) extends BenchmarkAlg[Unit]

object BenchmarkAlg {

  class BenchmarkOps[F[_]](implicit I: InjectK[BenchmarkAlg, F]) {
    def loadSettings(args: Array[String]): Free[F, Settings] =
      Free.inject[BenchmarkAlg, F](LoadSettings(args))

    def displayResults(result: Result): Free[F, Unit] =
      Free.inject[BenchmarkAlg, F](DisplayResults(result))
  }

  object BenchmarkOps {
    implicit def benchmark[F[_]](implicit I: InjectK[BenchmarkAlg, F]): BenchmarkOps[F] = new BenchmarkOps[F]()
  }

}