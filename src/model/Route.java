package model;

public class Route {
  private final String id;
  private final String short_name;
  private final String long_name;
  private final String route_type;

  public Route(String id, String short_name, String long_name, String route_type) {
    this.id = id;
    this.short_name = short_name;
    this.long_name = long_name;
    this.route_type = route_type;
  }

  public String getId() {
    return id;
  }

  public String getShortName() {
      return short_name;
  }

  public String getLongName() {
      return long_name;
  }

  public String getRouteType() {
    return route_type;
  }

  @Override
  public String toString() {
    return "Route{" +
             "id='" + id + '\'' +
             ", short_name='" + short_name + '\'' +
             ", long_name='" + long_name + '\'' +
             ", route_type='" + route_type + '\'' +
             '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Route route = (Route) o;
    return id.equals(route.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
  
}
