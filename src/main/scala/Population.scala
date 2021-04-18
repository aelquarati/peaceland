object Population {

  val CITIES_LIST = List("INCREDIBLY-PEACEFUL", "AMAZINGLY-PEACEFUL", "IMMENSELY-PEACEFUL", "EMINENTLY-PEACEFUL")
  val citizens = generateCitizens(200)
  val drones = generateDrones(50)

  def generateDrones(i:Int): List[Drone] = {
    if(i>=0)
      addDrone(i) :: generateDrones(i-1)
    else
      Nil
  }

  def addDrone(i:Int) : Drone = {
    //between -90 and 90
    val latitude = util.Random.nextInt(181) - 90
    //between -180 and 180
    val longitude = util.Random.nextInt(361) - 180

    new Drone(i, latitude, longitude)
  }

  def generateCitizens(i:Int): List[Citizen] = {
    if(i>=0)
      addCitizen(i) :: generateCitizens(i-1)
    else
      Nil
  }

  def addCitizen(i:Int) : Citizen = {
    val age = util.Random.nextInt(80)
    val score = util.Random.nextInt(100)
    val indexCity = util.Random.nextInt(4)

    Citizen(i, age, CITIES_LIST(indexCity), score)
  }

}
