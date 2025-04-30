package model;

public class Stop {
  private final String id;
  private final String name;
  private final double lat;
  private final double lon;

  public Stop(String id, String name, double lat, double lon) {
    this.id = id;
    this.name = name;
    this.lat = lat;
    this.lon = lon;
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
