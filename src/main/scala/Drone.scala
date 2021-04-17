class Drone(val id: Int, val latitude: Float, val longitude: Float) {

  val WORDS_LIST = List("rebellion", "mad", "go on strike", "government", "politic", "insubordination", "insurgency", "revolution", "insurrection")
  val max = WORDS_LIST.length

  def move(x:Float, y:Float): Drone = {
    new Drone(id, latitude+x, longitude+y);
  }

  def createMessage(citizen: Citizen) = {
     new DroneMessage(id, citizen.id, getRecognizedWords(max))
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
