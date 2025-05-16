package model;

/**
 * This class represents a transport route (like a bus or train line).
 * It just stores some basic info: ID, short/long name, and type (e.g. bus, tram, etc).
 */
public class Route {
  private final String id;
  private final String short_name;
  private final String long_name;
  private final String route_type;

  // Constructor for Route.
  public Route(String id, String short_name, String long_name, String route_type) {
    this.id = id;
    this.short_name = short_name;
    this.long_name = long_name;
    this.route_type = route_type;
  }

  // returns the route's ID
  public String getId() {
    return id;
  }

  // getter for the route's short name
  public String getShortName() {
      return short_name;
  }

  // getter for the route's long name
  public String getLongName() {
      return long_name;
  }

  // getter for the route's type
  public String getRouteType() {
    return route_type;
  }

  // returns the Route into a readable string
  @Override
  public String toString() {
    return "Route{" +
             "id='" + id + '\'' +
             ", short_name='" + short_name + '\'' +
             ", long_name='" + long_name + '\'' +
             ", route_type='" + route_type + '\'' +
             '}';
  }

  /**
   * Checks equality between this route and another object based on route ID.
   * Two routes are equal if their IDs are the same.
   *
   * @param o the other object to compare to
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Route route = (Route) o;
    return id.equals(route.id);
  }

  // hashcode based on id
  // This is important: equal objects must have the same hash code.
  @Override
  public int hashCode() {
    return id.hashCode();
  }
  
}
