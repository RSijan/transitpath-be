import java.util.Arrays;
import java.util.List;
import java.util.Map;
import model.*;
import pathfinder.*;
import utils.InputHandler;

public class Main {

  public static void main(String[] args) {
    System.out.println("========================================================");
    System.out.println("Welcome to the Transit Path Finder!");
    System.out.println("--------------------------------------------------------");
    System.out.println("Please wait while we load the transit data...");
    System.out.println("--------------------------------------------------------");

    String base_GTFS_directory = "GTFS";
    List<String> agencies = Arrays.asList("DELIJN", "SNCB", "STIB", "TEC");

    List<String> dir_list = agencies.stream()
            .map(agency -> base_GTFS_directory + "/" + agency)
            .toList();

    Parser parser = new Parser();
    boolean success = parser.loadData(dir_list);

    System.out.println("\n========================================================");
    if (success) {
      System.out.println("Transit data parsing process SUCCESSFUL.");
      Map<String, Route> routes = parser.getRoutesMap();
      Map<String, Stop> stops = parser.getStopsMap();
      Map<String, Trip> trips = parser.getTripsMap();
      Map<String, List<StopTime>> stop_times = parser.getStopTimesMap();

      GraphBuilder graphBuilder = new GraphBuilder(routes, stops, trips, stop_times);
      graphBuilder.buildGraph();
      Map<String, List<Edge>> graph = graphBuilder.getGraph();
      System.out.println("Graph construction process SUCCESSFUL.");
      System.out.println("Transit data loaded successfully.");
      System.out.println("--------------------------------------------------------");

      
      ShortestPathFinder shortestPathFinder = new ShortestPathFinder(graph, routes, stops, trips);

      InputHandler inputHandler = new InputHandler(stops);
      inputHandler.handleInput();
      String start_stop_id = inputHandler.getStartingStopId();
      String destination_stop_id = inputHandler.getDestinationStopId();
      int departure_time_seconds = inputHandler.getDepartureTimeSeconds();
      int preference = inputHandler.getPreference();

      Stop start_stop = stops.get(start_stop_id);
      Stop destination_stop = stops.get(destination_stop_id);

      System.out.println("--------------------------------------------------------");
      System.out.println("Finding path from " + start_stop.getName() + " to " + destination_stop.getName());
      System.out.println("--------------------------------------------------------");

      List<String> result = shortestPathFinder.aStar(start_stop_id, destination_stop_id, departure_time_seconds, preference);
  

      for (String path: result) {
        System.out.println(path);
      }

    } else {
      System.err.println("Data loading process FAILED .");
    }
    System.out.println("========================================================");
  }
}