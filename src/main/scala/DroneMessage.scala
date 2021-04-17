case class DroneMessage(droneId: Int, citizenId: Int, words: String) extends Serializable {
  override def toString: String = {
      "Drone_id: "+droneId+ " Citizen_id: "+citizenId+ " Words: "+words
  }

}
