package model;
/**
 * Represents a trip edge in a public transport system.
 * This class include informations about the target stop, departure time, arrival time,
 * trip ID, and route type.
 */

public class TripEdge implements Edge {
  private final Stop target_stop;
  private final int departure_time_sec;
  private final int arrival_time_sec;
  private final String trip_id;
  private final String route_type;

  // Constructor for TripEdge.
  public TripEdge(Stop target_stop, int departure_time_sec, int arrival_time_sec, String trip_id, String route_type) {
    this.target_stop = target_stop;
    this.departure_time_sec = departure_time_sec;
    this.arrival_time_sec = arrival_time_sec;
    this.trip_id = trip_id;
    this.route_type = route_type;
  }

  // Getter for the target stop.
  @Override
  public Stop getTarget() { return target_stop; }

  // getter for the vehicle departs (in seconds)
  public int getDepartureTimeSec() { return departure_time_sec; }

  // getter for the vehicle arrives at the target stop (in seconds)
  public int getArrivalTimeSec() { return arrival_time_sec; }

  // getter for the trip ID this edge is part of
  public String getTripId() { return trip_id; }

  // getter for the type of route (e.g. "bus", "metro", etc.)
  public String getRouteType() { return route_type; }

  // turns the TripEdge into a readable string
  @Override
  public String toString() {
    return String.format("TripEdge -> %s (Trip: %s, Depart: %ds, Arrive: %ds, Type: %s)", target_stop.getId(),
            trip_id, departure_time_sec, arrival_time_sec, route_type);
  }
}