class Drone(val id: String, val latitude: Float, val longitude: Float) {

  def move(x:Float, y:Float): Drone = {
    new Drone(id, latitude+x, longitude+y);
  }
}
