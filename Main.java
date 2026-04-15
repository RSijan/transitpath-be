import java.util.Arrays;
import java.util.List;
import java.util.Map;

import graphbuilder.*;
import model.*;
import parser.*;
import pathfinder.*;
import utils.InputHandler;

public class Main {

  public static void main(String[] args) {
    try {
      System.out.println("========================================================");
      System.out.println("Welcome to the Fastest Transit Path Finder!");
      System.out.println("Please wait while we load the transit data...");
      long transit_load_start = System.nanoTime();
      System.out.println("--------------------------------------------------------");

      String base_GTFS_directory = "GTFS";
      List<String> agencies = Arrays.asList("DELIJN", "SNCB", "STIB", "TEC");

      List<String> dir_list = agencies.stream()
              .map(agency -> base_GTFS_directory + "/" + agency)
              .toList();

      Parser parser = new Parser();
      boolean success = parser.loadData(dir_list);

      if (success) {
        Map<String, Route> routes = parser.getRoutesMap();
        Map<String, Stop> stops = parser.getStopsMap();
        Map<String, Trip> trips = parser.getTripsMap();
        Map<String, List<StopTime>> stop_times = parser.getStopTimesMap();

        GraphBuilder graphBuilder = new GraphBuilder(routes, stops, trips, stop_times);
        graphBuilder.buildGraph();
        Map<String, List<Edge>> graph = graphBuilder.getGraph();

        double transit_load_duration = (System.nanoTime() - transit_load_start) / 1e9;
        System.out.printf("Transit data loaded successfully in %.3f s.%n", transit_load_duration);
        System.out.println("--------------------------------------------------------");

        
        ShortestPathFinder shortestPathFinder = new ShortestPathFinder(graph, routes, stops, trips);
        InputHandler inputHandler = new InputHandler(stops);

        boolean running = true;
        while (running) {
          try {
            findPath(shortestPathFinder, inputHandler, stops);
          } catch (Exception e) {
            System.err.println("An error occurred while finding the path: " + e.getMessage());
            System.out.println("Quitting the program!");
            break;
          }

          System.out.println("--------------------------------------------------------");
          System.out.println("Would you like to find another fastest path? (y/n)");
          try {
            running = inputHandler.askForAnotherPath();
          } catch (Exception e) {
            System.err.println("An error occurred while reading your input: " + e.getMessage());
            System.out.println("Quitting the program. Thank you for using the Fastest Transit Path Finder!");
            running = false;
          }
        }

        System.out.println("Thank you for using the Fastest Transit Path Finder!");
      } else {
        System.err.println("Data loading process FAILED .");
      }
      System.out.println("========================================================");
    } catch (Exception e) {
      System.err.println("An error occurred during the execution of the program: " + e.getMessage());
      System.out.println("Quitting the program. Thank you for using the Fastest Transit Path Finder!");
    }
  }

  private static void findPath(ShortestPathFinder shortestPathFinder, InputHandler inputHandler, Map<String, Stop> stops) {
    try {
      inputHandler.handleInput();
    } catch (Exception e) {
      throw e;
    }
    String start_stop_id = inputHandler.getStartingStopId();
    String destination_stop_id = inputHandler.getDestinationStopId();
    int departure_time_seconds = inputHandler.getDepartureTimeSeconds();
    int preference = inputHandler.getPreference();

    Stop start_stop = stops.get(start_stop_id);
    Stop destination_stop = stops.get(destination_stop_id);

    System.out.println("--------------------------------------------------------");
    System.out.println(String.format("Determining fastest path from %s to %s at %s",start_stop.getName(), destination_stop.getName(), formatTime(departure_time_seconds)));
    System.out.println("--------------------------------------------------------");
    List<String> result = shortestPathFinder.aStar(start_stop_id, destination_stop_id, departure_time_seconds, preference);

    // Print the path
    if (result.isEmpty()) {
      System.out.println("No path found for the provided input.");
      return;
    }

    for (String path : result) {
      System.out.println(path);
    }
  }

  private static String formatTime(int seconds) {
    int hours = seconds / 3600;
    int minutes = (seconds % 3600) / 60;
    int secs = seconds % 60;
    return String.format("%02d:%02d:%02d", hours, minutes, secs);
  }
}