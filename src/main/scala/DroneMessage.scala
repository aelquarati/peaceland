case class DroneMessage(droneId: Int, citizenId: Int, score: Int, lat: Float, long: Float, words: String) extends Serializable {
  override def toString: String = {
      "Drone_id:"+droneId+ "; Citizen_id:"+citizenId+ "; Score:"+ score + "; Latitude:" +lat+ "; Longitude:"+ long +"; Words:"+words
  }

}
