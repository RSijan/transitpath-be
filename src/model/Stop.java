package model;

/**
 * Represents a single stop (like a bus or train stop).
 * 
 * Each stop has an ID, a name, and a location (latitude and longitude).
 * There's also a method to check if another stop is close enough to walk to.
 */
public class Stop {
  private final String id;
  private final String name;
  private final double lat;
  private final double lon;

  // Max distance (in meters) that we consider "walkable"
  private static final double MAX_PROXIMITY_WALKING_DISTANCE_METRES = 2000;
  // Convert meters to degrees of latitude (1 degree ≈ 111km)
  private static final double MAX_DIST_DEG_LAT = MAX_PROXIMITY_WALKING_DISTANCE_METRES / 111000.0;
  // Constructor for Stop.
  public Stop(String id, String name, double lat, double lon) {
    this.id = id;
    this.name = name;
    this.lat = lat;
    this.lon = lon;
  }

  /**
   * Checks if another stop is "nearby" (within walking distance).
   * 
   * We don't calculate actual distances with haversine or anything fancy.
   * Instead, we just use a quick lat/lon bounding box.
   * 
   * @param other the stop we're checking against
   * @return true if the other stop is close enough, false if not
   */
  public boolean isWithinProximity(Stop other) {
    if (other == null || this.equals(other)) {
      return false;
    }

    // we adjust longitude comparison based on latitude (earth isn't a square)
    double cosLat = Math.cos(Math.toRadians(this.lat));
    double maxDisDegLon = MAX_PROXIMITY_WALKING_DISTANCE_METRES / (111000.0 * cosLat);

    // return false if The other stop is outside the bounding box that we set (max walking distance)
    return !(Math.abs(this.lat - other.lat) > MAX_DIST_DEG_LAT || Math.abs(this.lon - other.lon) > maxDisDegLon);
  }

  // returns the stop ID
  public String getId() {
    return id;
  }

  // returns the stop name
  public String getName() {
      return name;
  }

  // returns latitude
  public double getLat() {
      return lat;
  }

 // returns longitude
  public double getLon() {
      return lon;
  }

  // turns the Stop into a readable string
  // Make it easy to print log, debug ...etc
  @Override
    public String toString() {
        return "Stop{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", lat=" + lat + ", lon=" + lon + '}';
    }

  /**
   * Checks equality between this stop and another object based on stop ID.
   * Two stops are equal if their IDs match.
   *
   * @param o The object to compare.
   * @return true if the objects are of the same type and have the same ID, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Stop stop = (Stop) o;
      return id.equals(stop.id);
  }

  // hashcode based on id
  @Override
  public int hashCode() {
      return id.hashCode();
  }
}
