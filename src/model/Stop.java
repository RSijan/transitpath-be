package model;

public class Stop {
  private final String id;
  private final String name;
  private final double lat;
  private final double lon;

  // Farthest another stop can be at a walking distance to this stop
  // cuz it takes too long to walk to that stop
  private static final double MAX_PROXIMITY_WALKING_DISTANCE_METRES = 2000;
  // Converting that distance to degree
  private static final double MAX_DIST_DEG_LAT = MAX_PROXIMITY_WALKING_DISTANCE_METRES / 111000.0;

  public Stop(String id, String name, double lat, double lon) {
    this.id = id;
    this.name = name;
    this.lat = lat;
    this.lon = lon;
  }

  public boolean isWithinProximity(Stop other) {
    if (other == null || this.equals(other)) {
      return false;
    }

    double cosLat = Math.cos(this.lat);
    double maxDisDegLon = MAX_PROXIMITY_WALKING_DISTANCE_METRES / (111000.0 * cosLat);
    // return false if The other stop is outside the bounding box that we set (max walking distance)
    return !(Math.abs(this.lat - other.lat) > MAX_DIST_DEG_LAT || Math.abs(this.lon - other.lon) > maxDisDegLon);

  }

  public String getId() {
    return id;
  }

  public String getName() {
      return name;
  }

  public double getLat() {
      return lat;
  }

  public double getLon() {
      return lon;
  }

  @Override
    public String toString() {
        return "Stop{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", lat=" + lat +
               ", lon=" + lon +
               '}';
    }
  
  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Stop stop = (Stop) o;
      return id.equals(stop.id);
  }

  @Override
  public int hashCode() {
      return id.hashCode();
  }
}
