package pgawrys.totem

final case class Result(res: Seq[Double]) {

  def prettyPrint(): String = {
    val min = res min
    val max = res max
    val avg = res.sum / res.length
    val variance = res
      .map(x => math pow(x - avg, 2))
      .sum / res.length

    val stddev = math.sqrt(variance)

    s"min: $min | max: $max | avg: $avg | variance: $variance | stddev: $stddev"
  }
}
