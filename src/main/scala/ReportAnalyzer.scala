import Stream.ALERT_WORDS
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object ReportAnalyzer extends App {

  val spark = SparkSession
    .builder()
    .appName("peaceland")
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
  import spark.implicits._


  val SUSPICIOUS_WORDS = List("riot", "rebellion")

  def avgScoreOfPeopleSayingBadThings(): String = {
    val people = df.filter(row => saySuspiciousWords(row.get(5).toString))
    val distinctPeople = people.drop("DroneId", "Latitude", "Longitude", "Words").distinct()
    val avgScore = distinctPeople.select(avg($"Score")).map(row => row.get(0).toString).first()
    "AVERAGE SCORE OF PEOPLE SAYING BAG/SUSPICIOUS WORDS : " + avgScore
  }

  def saySuspiciousWords(words: String): Boolean = {
    val list = words.split(" ")
    !list.filter(word => SUSPICIOUS_WORDS.contains(word)).isEmpty
  }

  def topTenSuspiciousCitizen(): String = {
    val sortedList = df.groupBy($"CitizenId").agg($"")
  }


  def mostSensibleCity():String  = {
    val suspicious = df.filter(row => saySuspiciousWords(row.get(5).toString))

    //-180 0 et -90 0 incredibly
    //0 180 et 0 90   amazingly
    //-180 0 et 0 90  immensely
    //0 180 et 0 -90  eminently

    val inIncredibly = suspicious.filter($"Latitude" < 0 && $"Longitude" >= -180 && $"Longitude" < 0 && $"Longitude" >= -90).count.toInt
    val inAmazingly = suspicious.filter($"Latitude" <= 180 && $"Longitude" >= 0 && $"Longitude" <= 0 && $"Longitude" >= 90).count.toInt
    val inImmensely = suspicious.filter($"Latitude" < 0 && $"Longitude" >= -180 && $"Longitude" >= 0 && $"Longitude" <= 90).count.toInt
    val inEminently = suspicious.filter($"Latitude" <= 180 && $"Longitude" >= 0 && $"Longitude" < 0 && $"Longitude" > -90).count.toInt

    val list = List(inIncredibly, inAmazingly, inImmensely, inEminently)
    val maxWithIndex = list.zipWithIndex.maxBy(_._1)

    if (maxWithIndex._2 == 0) {
      "MOST SENSIBLE CITY: INCREDIBLY-PEACEFUL CITY \n   TOTAL OF " + inIncredibly + " DETECTED"
    }
    else if (maxWithIndex._2 == 1) {
      "MOST SENSIBLE CITY: AMAZINGLY-PEACEFUL CITY  \n   TOTAL OF " + inAmazingly + " DETECTED"
    }
    else if (maxWithIndex._2 == 2) {
      "MOST SENSIBLE CITY: IMMENSELY-PEACEFUL CITY  \n   TOTAL OF " + inImmensely + " DETECTED"
    }
    else {
      "MOST SENSIBLE CITY: EMINENTLY-PEACEFUL CITY  \n   TOTAL OF " + inEminently + " DETECTED"
    }
  }


  //import needed to use '$' notation

  import spark.implicits._



  //df.select("DroneId").show(10)


}
