package pgawrys.totem

final case class Settings(path: String,
                          iterations: Int = 5,
                          parquet: Boolean = false,
                          persist: Boolean = false,
                          benchmarkType: BenchmarkType = BenchmarkType.Filter,
                          limit: Option[Int] = None)
