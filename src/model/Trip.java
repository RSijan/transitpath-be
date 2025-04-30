package model;

public class Trip {
  private final String id;
  private final String route_id;

  public Trip(String id, String route_id) {
    this.id = id;
    this.route_id = route_id;
  }

  public String getId() {
    return id;
  }

  public String getRouteID() {
    return route_id;
  }

  @Override
  public String toString() {
    return "Trip{" +
            "id='" + id + '\'' +
            ", route_id='" + route_id + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Trip trip = (Trip) o;
    return id.equals(trip.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
