package pgawrys.totem

final case class Result(res: List[Double]) {

  def prettyPrint(): String = {
    val min = res min
    val max = res max
    val avg = res.sum / res.length
    val variance = res
      .map(x => math pow(x - avg, 2))
      .sum / res.length

    val stddev = math.sqrt(variance)
    val median = {
      val (lower, upper) = res.sortWith(_<_).splitAt(res.size / 2)
      if (res.size % 2 == 0) (lower.last + upper.head) / 2.0 else upper.head
    }

    s"min: $min | max: $max | avg: $avg | median: $median | variance: $variance | stddev: $stddev [s]"
  }
}
