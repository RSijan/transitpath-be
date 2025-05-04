import model.*;
import pathfinder.ShortestPathFinder;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class Main {

  public static void main(String[] args) {

    String base_GTFS_directory = "GTFS";
    List<String> agencies = Arrays.asList("DELIJN", "SNCB", "STIB", "TEC");

    List<String> dir_list = agencies.stream()
            .map(agency -> base_GTFS_directory + "/" + agency)
            .toList();

    System.out.println("========================================================");
    System.out.println("Attempting to load transit data from ALL agencies:");
    dir_list.forEach(path -> System.out.println("- " + path));
    System.out.println("========================================================");

    Parser parser = new Parser();

    boolean success = parser.loadData(dir_list);

    System.out.println("\n========================================================");
    if (success) {
      System.out.println("Data loading process completed successfully!");
      System.out.println("--------------------------------------------------------");

      Map<String, Route> routes = parser.getRoutesMap();
      Map<String, Stop> stops = parser.getStopsMap();
      Map<String, Trip> trips = parser.getTripsMap();
      Map<String, List<StopTime>> stop_times = parser.getStopTimesMap();

      long stop_times_count = 0;
      for (List<StopTime> stop_time_list : stop_times.values()) {
        for (StopTime stop_time : stop_time_list) {
          stop_times_count++;
        }
      }

      GraphBuilder graphBuilder = new GraphBuilder(routes, stops, trips, stop_times);
      graphBuilder.buildGraph();
      Map<String, List<Edge>> graph = graphBuilder.getGraph();

      ShortestPathFinder shortestPathFinder = new ShortestPathFinder(graph, routes, stops, trips);
      List<String> answer = shortestPathFinder.aStar("DELIJN-509014",
              "SNCB-S8866654",
              37800);
      System.out.println(answer.size());
      for (String s : answer) {
        System.out.println(s);
      }

    } else {
      System.err.println("Data loading process FAILED .");
    }
    System.out.println("========================================================");
  }
}