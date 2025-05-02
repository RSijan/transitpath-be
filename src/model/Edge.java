package model;

public interface Edge {
  Stop getTarget();
}

class TripEdge implements Edge {
  private final Stop target_stop;
  private final int departure_time_sec;
  private final int arrival_time_sec;
  private final String trip_id;
  private final String route_type;

  public TripEdge(Stop target_stop, int departure_time_sec, int arrival_time_sec, String trip_id, String route_type) {
    this.target_stop = target_stop;
    this.departure_time_sec = departure_time_sec;
    this.arrival_time_sec = arrival_time_sec;
    this.trip_id = trip_id;
    this.route_type = route_type;
  }

  @Override
  public Stop getTarget() { return target_stop; }

  public int getDepartureTimeSec() { return departure_time_sec; }

  public int getArrivalTimeSec() { return arrival_time_sec; }

  public String getTripId() { return trip_id; }

  public String getRouteType() { return route_type; }

  @Override
  public String toString() {
    return String.format("TripEdge -> %s (Trip: %s, Depart: %ds, Arrive: %ds, Type: %s)", target_stop.getId(),
            trip_id, departure_time_sec, arrival_time_sec, route_type);
  }
}

class WalkingEdge implements Edge {
  private final Stop target_stop;
  private final int duration_sec;

  public WalkingEdge(Stop target_stop, int duration_sec) {
    this.target_stop = target_stop;
    this.duration_sec = duration_sec;
  }

  @Override
  public Stop getTarget() { return target_stop; }

  public int getDurationSec() { return duration_sec; }

  @Override
  public String toString() {
    return String.format("WalkingEdge -> %s (Duration: %ds)", target_stop.getId(), duration_sec);
  }
}
