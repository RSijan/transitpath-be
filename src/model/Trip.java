package model;

/**
 * Represents a specific trip on a public transport route.
 * 
 * A trip is one complete run of a vehicle (like Bus #42 going from start to end).
 */
public class Trip {
  private final String id;
  private final String route_id;

  // Constructor for Trip.
  public Trip(String id, String route_id) {
    this.id = id;
    this.route_id = route_id;
  }

  // Getter for the trip ID.
  public String getId() {
    return id;
  }

  // Get the ID of the route this trip is part of
  public String getRouteID() {
    return route_id;
  }

  // turns the Trip into a readable string
  @Override
  public String toString() {
    return "Trip{" +
            "id='" + id + '\'' +
            ", route_id='" + route_id + '\'' +
            '}';
  }

  /**
   * Two trips are equal if they have the same trip ID.
   *
   * @param o the other object to compare to
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Trip trip = (Trip) o;
    return id.equals(trip.id);
  }

  // hashcode based on id
  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
