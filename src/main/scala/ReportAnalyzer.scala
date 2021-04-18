object ReportAnalyzer extends App{

val spark = SparkSession
  .builder()
  .appName("peaceland")
  .config("spark.master", "local")
  .getOrCreate()

val df = spark.read.format("csv")
  .option("sep", ";")
  .option("inferSchema", "true")
  .option("header", "true")
  .load("hdfs://localhost:9000/tmp/testcsv.csv")

//import needed to use '$' notation
  import spark.implicits._

  df.printSchema()

}
