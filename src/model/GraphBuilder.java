package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import utils.TransitDurationCalculator;

public class GraphBuilder {
  // Adjacency list
  private final Map<String, List<Edge>> graph = new HashMap<>();

  private final Map<String, Route> routes_;
  private final Map<String, Stop> stops_;
  private final Map<String, Trip> trips_;
  private final Map<String, List<StopTime>> stop_times_;

  public GraphBuilder(Map<String, Route> routes, Map<String, Stop> stops, Map<String, Trip> trips, Map<String, List<StopTime>> stop_times) {
    routes_ = routes;
    stops_ = stops;
    trips_ = trips;
    stop_times_ = stop_times;
  }

  public Map<String, List<Edge>> getGraph() {
    return graph;
  }

  public void buildGraph() {
    long start_time = System.nanoTime();
    long trip_start_time = System.nanoTime();
    graph.clear(); // Clear the graph before building it

    // Put every stop in the graph as a node
    // Also we use a synchronized list to avoid locking it each time we add an edge
    stops_.keySet().forEach(stopId ->
      graph.put(stopId, Collections.synchronizedList(new ArrayList<>()))
    );

    // put trip edges to graph
    for (var entry : trips_.entrySet()) {
      String trip_id = entry.getKey();
      List<StopTime> stop_times = stop_times_.get(trip_id);
      for (int i = 0; i < stop_times.size() - 1; i++) {
        // Current stop time & Next stop time
        StopTime stop_time = stop_times.get(i);
        StopTime next_stop_time = stop_times.get(i + 1);

        // Depart time & Arrival Time
        int departure_time_sec = stop_time.getDepartureTimeSeconds();
        int arrival_time_sec = next_stop_time.getDepartureTimeSeconds();

        // Current stop & next stop
        String current_stop_id = stop_time.getStopId();
        String next_stop_id = next_stop_time.getStopId();
        Stop next_stop = stops_.get(next_stop_id);

        // Route type
        String route_id = trips_.get(trip_id).getRouteID();
        String route_type = routes_.get(route_id).getRouteType();

        // Make the edge to the graph
        TripEdge edge = new TripEdge(next_stop, departure_time_sec, arrival_time_sec, trip_id, route_type);

        // Add the edge to the graph
        // We use computeIfAbsent here to insert a list if the stop doesn't exist yet in the graph
        graph.computeIfAbsent(current_stop_id, k -> new ArrayList<>()).add(edge);
      }
    }

    long trip_end_time = System.nanoTime();
    double trip_duration_s = (trip_end_time - trip_start_time) / 1e9;
    System.out.println("--------------------------------------------------------");
    System.out.printf("Finished trip edges building successfully. Total time taken: %.3f s.%n", trip_duration_s);

    long walking_start_time = System.nanoTime();

    // Parallely build walking edges for every stop
    List<Stop> stop_list = new ArrayList<>(stops_.values()); // Get all stops
    int n = stop_list.size(); // Number of stops

    // We are parallelizing the loop to speed up the process:
    // We use IntStream.range to create a stream of integers from 0 to n-1
    // and then we use parallel() to process them in parallel
    // For each stop, we check if it's within proximity of any other stop
    // If it is, we calculate the walking duration and add the edge to the graph
    IntStream.range(0, n).parallel().forEach(i -> {
      Stop stopA = stop_list.get(i);
      for (int j = i + 1; j < n; j++) {
        Stop stopB = stop_list.get(j);
        if (stopA.isWithinProximity(stopB)) {
          int walkDuration = TransitDurationCalculator.calculateWalkDuration(stopA, stopB);
          if (walkDuration != Integer.MAX_VALUE) {
            String stopA_id = stopA.getId(), stopB_id = stopB.getId();
            graph.get(stopA_id).add(new WalkingEdge(stopB, walkDuration));
            graph.get(stopB_id).add(new WalkingEdge(stopA, walkDuration));
          }
        }
      }
    });
    // DEBUG
    long walking_end_time = System.nanoTime();
    double walking_duration_s = (walking_end_time - walking_start_time) / 1e9;
    System.out.printf("Finished walking edges building successfully. Total time taken: %.3f s.%n", walking_duration_s);

    long end_time = System.nanoTime();
    double total_duration_s = (end_time - start_time) / 1e9;
    System.out.printf("Finished graph building successfully. Total time taken: %.3f s.%n", total_duration_s);
    System.out.println("--------------------------------------------------------");

  }
}
