package pgawrys.totem

final case class Settings(path: String,
                          iterations: Int = 5,
                          parquet: Boolean = false,
                          persist: Boolean = false,
                          benchmarkType: BenchmarkType = BenchmarkType.Filter,
                          limit: Option[Int] = None) {

  def prettyString: String =
    s"Benchmark Settings: \npath: $path\niterations: $iterations\nparquet: $parquet\npersist: $persist\ntype: $benchmarkType\nlimit: $limit"
}