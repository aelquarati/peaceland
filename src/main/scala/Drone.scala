
class Drone(val id: Int, val latitude: Float, val longitude: Float) {

  val WORDS_LIST = List("riot", "rebellion", "mad", "government", "politic", "insubordination", "insurgency", "revolution", "insurrection")
  val max = WORDS_LIST.length

  def move(x:Float, y:Float): Drone = {
    if(latitude+x > 90 || latitude+x < -90) {
      move(-x, y)
    }
    else if(longitude+y > 180 || longitude+y < -180 ) {
      move(x, -y)
    }
    new Drone(id, latitude+x, longitude+y);
  }

  def createMessage(citizen: Citizen) = {
     DroneMessage(id, citizen.id, citizen.peaceScore, getRandomInt(181) - 90, getRandomInt(361 ) -180, getRecognizedWords(max))
  }

  def getRecognizedWords(i:Int) : String = {
      if (i>=0)
        getWord() + " " + getRecognizedWords(i-1)
      else
        ""
  }

  def getWord(): String = {
    val random = getRandomInt(max)
    WORDS_LIST(random)
  }

  def getRandomInt(MaxValue: Int): Int = util.Random.nextInt(MaxValue)
}
