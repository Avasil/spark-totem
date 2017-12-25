package pgawrys.totem

sealed trait BenchmarkType

object BenchmarkType {

  def apply(i: Int): BenchmarkType = i match {
    case 2 => Lorentz
    case 3 => Filter
    case _ => Filter
  }

  case object Filter extends BenchmarkType

  case object HeavyComputation extends BenchmarkType

  case object Lorentz extends BenchmarkType

}