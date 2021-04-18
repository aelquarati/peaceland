import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.functions.when
import org.apache.spark.sql.{DataFrame, SparkSession}

object ReportAnalyzer extends App{

 val spark = SparkSession
   .builder()
   //.appName("peaceland")
   .config("spark.master", "local")
   .getOrCreate()


  lazy val df = spark.read.format("csv")
    .option("sep", ";")
    .option("inferSchema", "true")
    .option("header", "true")
    .load("hdfs://localhost:9000/tmp/testcsv.csv")

  df.show()
  println("count" + df.count())




//import needed to use '$' notation
 // import spark.implicits._



  //df.select("DroneId").show(10)


}
