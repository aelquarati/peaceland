import Stream.ALERT_WORDS
import org.apache.commons.codec.Encoder
import org.apache.commons.codec.binary.Base64
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object ReportAnalyzer extends App {

  Logger.getLogger("org").setLevel(Level.ERROR)

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
    val people = df.filter(row => saySuspiciousWords(row.getString(7)))
    val distinctPeople = people.drop("Date", "Hour", "DroneId", "Latitude", "Longitude", "Words").distinct()
    val listScore = distinctPeople.select($"PeaceScore").map(row => row.getInt(0)).collectAsList()
    val avgScore = listScore.foldLeft(0)(_+_) / listScore.size()
    "AVERAGE SCORE OF PEOPLE SAYING BAG/SUSPICIOUS WORDS : " + avgScore
  }

  def saySuspiciousWords(words: String): Boolean = {
    val list = words.split(" ")
    !list.filter(word => SUSPICIOUS_WORDS.contains(word)).isEmpty
  }

  def topTenPersonWithBadestScoreSayingBadWords(): String = {
    val people = df.filter(row => saySuspiciousWords(row.getString(7)))
    val distinctPeople = people.drop("Date", "Hour","DroneId", "Latitude", "Longitude", "Words").distinct()
    val array = distinctPeople.sort(asc("PeaceScore")).head(10).map(row => "CITIZEN ID :" +  row.get(0).toString + " ; SCORE :"+ row.get(1).toString+ "\n")
    array.mkString(" ")
  }

  def mostSensibleCity():String  = {
    val suspicious = df.filter(row => saySuspiciousWords(row.getString(7)))

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
      "MOST SENSIBLE CITY: INCREDIBLY-PEACEFUL CITY \n   TOTAL OF " + inIncredibly + " DRONE REPORTS CONTAINING SUSPICIOUS WORDS RECORDED"
    }
    else if (maxWithIndex._2 == 1) {
      "MOST SENSIBLE CITY: AMAZINGLY-PEACEFUL CITY  \n   TOTAL OF " + inAmazingly + " DRONE REPORTS CONTAINING SUSPICIOUS WORDS RECORDED"
    }
    else if (maxWithIndex._2 == 2) {
      "MOST SENSIBLE CITY: IMMENSELY-PEACEFUL CITY  \n   TOTAL OF " + inImmensely + " DRONE REPORTS CONTAINING SUSPICIOUS WORDS RECORDED"
    }
    else {
      "MOST SENSIBLE CITY: EMINENTLY-PEACEFUL CITY  \n   TOTAL OF " + inEminently + " DRONE REPORTS CONTAINING SUSPICIOUS WORDS RECORDED"
    }
  }

  def personMostRecordedSayingBadWords() = {
    val people = df.filter(row => saySuspiciousWords(row.getString(7)))
    val personWithNumber = people.groupBy("CitizenID").agg(count("*").as("count"))
    val person = personWithNumber.sort(desc("count")).first()
    "CITIZEN WHOSE BEEN MOST RECORDED SAYING BAD WORDS = CITIZEN ID :" + person.getInt(0) + " ; COUNT: "+person.getLong(1)
  }

  println(topTenPersonWithBadestScoreSayingBadWords())
  println(avgScoreOfPeopleSayingBadThings())
  println(mostSensibleCity())
 println(personMostRecordedSayingBadWords())
}
