package pathfinder;

import java.util.*;
import model.*;
import utils.TransitDurationCalculator;

public class ShortestPathFinder {
  private final Map<String, List<Edge>> graph_;
  private final Map<String, Route> routes_;
  private final Map<String, Stop> stops_;
  private final Map<String, Trip> trips_;

  public ShortestPathFinder(Map<String, List<Edge>> graph,
                            Map<String, Route> routes,
                            Map<String, Stop> stops,
                            Map<String, Trip> trips) {
    graph_ = graph;
    routes_ = routes;
    stops_ = stops;
    trips_ = trips;
  }

  public List<String> aStar(String start_stop_id, String target_stop_id, int departureTimeSec, int preference){
    Stop target_stop = stops_.get(target_stop_id);
    Map<String, Integer> h_cache = new HashMap<>();
    for (String id : stops_.keySet()) {
      Stop s = stops_.get(id);
      int estimateDuration = TransitDurationCalculator.calculateTotalDuration(s, target_stop);
      h_cache.put(id, estimateDuration);
    }

    PriorityQueue<State> open = new PriorityQueue<>(
      Comparator.comparingInt((State s) -> s.total_elapsed_time + h_cache.get(s.stop_id))
          .thenComparingInt(s -> s.current_time_sec)
    );
    open.add(new State(start_stop_id, departureTimeSec, 0, null, null));

    Map<String, State> best = new HashMap<>();

    while(!open.isEmpty()) {
      State current = open.poll();

      if (current.stop_id.equals(target_stop_id)) {
        return reconstructItinerary(current);
      }

      if ((best.containsKey(current.stop_id) &&
          (best.get(current.stop_id).total_elapsed_time) <= current.total_elapsed_time)) {
        continue;
      }
      best.put(current.stop_id, current);

      for (Edge edge : graph_.get(current.stop_id)) {
        if (edge instanceof TripEdge trip_edge) {
          int departure = trip_edge.getDepartureTimeSec();
          if (departure < current.current_time_sec) continue;

          int wait_time = departure - current.current_time_sec;
          int ride_time = trip_edge.getArrivalTimeSec()   - trip_edge.getDepartureTimeSec();
          int new_elapsed_time = current.total_elapsed_time + wait_time + ride_time;
          int arrival_time = current.current_time_sec + wait_time + ride_time;

          State next = new State(
                  trip_edge.getTarget().getId(),
                  arrival_time,
                  new_elapsed_time,
                  current, edge
          );

          open.add(next);
        } else if (edge instanceof WalkingEdge walking_edge) {
          int duration = walking_edge.getDurationSec();
          State next = new State(
                  walking_edge.getTarget().getId(),
                  current.current_time_sec + duration,
                  current.total_elapsed_time + duration,
                  current, edge
          );
          open.add(next);
        }
      }
    }
    return List.of();
  }

  private List<String> reconstructItinerary(State goalState) {
    List<State> state_chain = new ArrayList<>();
    // Add all the previous states to a list
    for (State current_state = goalState; current_state != null; current_state = current_state.previous_state) {
      state_chain.add(current_state);
    }
    Collections.reverse(state_chain); // Since we now have all the states from start to end
    // We just have to print them properly

    List<String> result = new ArrayList<>();
    int i = 1;

    // This is for a special case: if the last two states are walking edges and they have the same stop names, remove the last one
    // This specially handles the case where the person has to walk to a stop with the same name at the end of the trip
    if (state_chain.size() > 1) {
      State last = state_chain.get(state_chain.size()-1);
      if (last.edge_taken instanceof WalkingEdge) {
          State secondLast = state_chain.get(state_chain.size()-2);
          String lastStopName = stops_.get(last.stop_id).getName();
          String secondLastStopName = stops_.get(secondLast.stop_id).getName();
          if (lastStopName.equals(secondLastStopName)) {
              state_chain.remove(state_chain.size()-1);
          }
      }
  }

    while (i < state_chain.size()) {
      State previous_state = state_chain.get(i-1);
      State current_state = state_chain.get(i);
      Edge edge_taken = current_state.edge_taken;

      if (edge_taken instanceof WalkingEdge walkingEdge) {
        String previous_stop_name = stops_.get(previous_state.stop_id).getName();
        String current_stop_name = stops_.get(current_state.stop_id).getName();

        int walking_duration = walkingEdge.getDurationSec();
        if (previous_stop_name.equals(current_stop_name) && walking_duration < 60) {
          i++;
          continue;
        }

        result.add(String.format("Walk from %s (%s) to %s (%s)",
                previous_stop_name,
                formatTime(previous_state.current_time_sec),
                current_stop_name,
                formatTime(current_state.current_time_sec)));
        i++;
        continue;
      }
      TripEdge trip_edge = (TripEdge)edge_taken;
      String agency = getAgencyFromId(trip_edge.getTripId());
      String trip_id = trip_edge.getTripId();
      String route_id = trips_.get(trip_id).getRouteID();
      String route_number = routes_.get(route_id).getShortName();
      String route_type = routes_.get(route_id).getRouteType();
      String previous_stop_name = stops_.get(previous_state.stop_id).getName();
      String departure_time = formatTime(trip_edge.getDepartureTimeSec());

      int j = i + 1;
      State last_state = current_state;
      while(j < state_chain.size()) {
        Edge nextEdge = state_chain.get(j).edge_taken;
        if (!(nextEdge instanceof TripEdge)) break;
        TripEdge te = (TripEdge)nextEdge;

        String next_trip_id = te.getTripId();
        String next_route_id = trips_.get(next_trip_id).getRouteID();
        String next_route_number = routes_.get(next_route_id).getShortName();
        String next_route_type = routes_.get(next_route_id).getRouteType();
        String next_agency = getAgencyFromId(next_trip_id);

        if (!next_agency.equals(agency) ||
            !next_route_number.equals(route_number) ||
            !next_route_type.equals(route_type)) {
            break;
        }
        last_state = state_chain.get(j);
        j++;
      }

      TripEdge last_edge = (TripEdge)last_state.edge_taken;
      String current_stop_name = stops_.get(last_state.stop_id).getName();
      String arrival_time = formatTime(last_edge.getArrivalTimeSec());

      result.add(String.format("Take %s %s %s from %s (%s) to %s (%s)",
              agency,
              route_type,
              route_number,
              previous_stop_name,
              departure_time,
              current_stop_name,
              arrival_time));
      i = j;
    }
    return result;
  }

  private String formatTime(int seconds) {
    int hour = seconds / 3600;
    int min = seconds % 3600 / 60;
    int sec = seconds % 60;
    return String.format("%02d:%02d:%02d", hour, min, sec);
  }

  private String getAgencyFromId(String tripId) {
    if (tripId.startsWith("STIB")) return "STIB";
    if (tripId.startsWith("SNCB")) return "SNCB";
    if (tripId.startsWith("DELIJN")) return "DELIJN";
    if (tripId.startsWith("TEC")) return "TEC";
    return "UNKNOWN";
  }

  private class State {
    public final String stop_id;
    public final int current_time_sec;
    public final int total_elapsed_time;
    public final State previous_state;
    public final Edge edge_taken;

    public State(String stop_id, int current_time_sec, int total_elapsed_time, State previous_state, Edge edge_taken) {
      this.stop_id = stop_id;
      this.current_time_sec = current_time_sec;
      this.total_elapsed_time = total_elapsed_time;
      this.previous_state = previous_state;
      this.edge_taken = edge_taken;
    }
}

}
