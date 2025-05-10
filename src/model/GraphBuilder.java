package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import utils.TransitDurationCalculator;

public class GraphBuilder {
  // Adjacency list
  private final Map<String, List<Edge>> graph = new HashMap<>();

  private final Map<String, Route> routes_;
  private final Map<String, Stop> stops_;
  private final Map<String, Trip> trips_;
  private final Map<String, List<StopTime>> stop_times_;

  private static final Logger LOGGER = Logger.getLogger(GraphBuilder.class.getName());

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
    LOGGER.info("Starting graph construction...");
    long start_time = System.nanoTime();

    // Put every stop in the graph as a node
    for (String stop_id : stops_.keySet()) {
      graph.put(stop_id, new ArrayList<>());
    }
    LOGGER.info(String.format("Initialized graph with %d potential nodes. ", graph.size()));

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
        Stop current_stop = stops_.get(current_stop_id);
        String next_stop_id = next_stop_time.getStopId();
        Stop next_stop = stops_.get(next_stop_id);

        // Route type
        String route_id = trips_.get(trip_id).getRouteID();
        String route_type = routes_.get(route_id).getRouteType();

        TripEdge edge = new TripEdge(next_stop, departure_time_sec, arrival_time_sec, trip_id, route_type);
        graph.computeIfAbsent(current_stop_id, k -> new ArrayList<>()).add(edge);
      }
    }

    // Parallely build walking edges for every stop
    stops_.values().parallelStream().forEach(stopA -> {
      stops_.values().forEach(stopB -> {
        if (!stopA.equals(stopB) && stopA.isWithinProximity(stopB)) {
          int walkDuration = TransitDurationCalculator.calculateWalkDuration(stopA, stopB);
          if (walkDuration != Integer.MAX_VALUE) {
            synchronized (graph) {
              graph.get(stopA.getId()).add(new WalkingEdge(stopB, walkDuration));
              graph.get(stopB.getId()).add(new WalkingEdge(stopA, walkDuration));
            }
          }
        }
      });
    });

    // DEBUG
    long end_time = System.nanoTime();
    long duration_s = TimeUnit.NANOSECONDS.toSeconds(end_time - start_time);
    LOGGER.info("Graph construction complete.");
    LOGGER.info(String.format("Total graph build duration: %d s.", duration_s));

    long total_edges = 0;
    for (List<Edge> edge : graph.values()) {
      total_edges += edge.size();
    }

    LOGGER.info(String.format("Total number of edges in graph: %d", total_edges));
    LOGGER.info(String.format("Total number of nodes in graph: %d", graph.size()));
  }
}
