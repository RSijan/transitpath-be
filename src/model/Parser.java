package model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {

  private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

  private final Map<String, Route> routes_map = new HashMap<>();
  private final Map<String, Stop> stops_map = new HashMap<>();
  private final Map<String, Trip> trips_map = new HashMap<>();
  private final Map<String, List<StopTime>> stop_times_map = new HashMap<>();

  public void clearData() {
    routes_map.clear();
    stops_map.clear();
    trips_map.clear();
    stop_times_map.clear();
    LOGGER.info("All maps have been emptied.");
  }

  public boolean loadData(List<String> directoryPath) {
    LOGGER.log(Level.INFO, "Starting data loading from directory: {0}", directoryPath);
    long total_start_time = System.nanoTime();
    clearData();

    boolean success = true;

    try {
      for (String path : directoryPath) {
        LOGGER.log(Level.INFO, "Loading data from: {0}", path);
        Path dir = Paths.get(path);
        if (!Files.isDirectory(dir)) {
          LOGGER.log(Level.SEVERE, "Path {0} is not a directory.", path);
          success = false;
          continue;
        }
        String route_file_path = Paths.get(path, "routes.csv").toString();
        String stop_file_path = Paths.get(path, "stops.csv").toString();
        String trip_file_path = Paths.get(path, "trips.csv").toString();
        String stop_time_file_path = Paths.get(path, "stop_times.csv").toString();

        parseRoutes(route_file_path);
        parseStops(stop_file_path);
        parseTrips(trip_file_path);
        parseStopTimes(stop_time_file_path);
      }

      if (success) {
        sortStopTimesBySequence();
        long total_end_time = System.nanoTime();
        long total_duration_s = TimeUnit.NANOSECONDS.toSeconds(total_end_time - total_start_time);
        LOGGER.log(Level.INFO, "Finished all data loading and processing successfully from {0} sources. Total time: {1} s.", new Object[]{directoryPath.size(), total_duration_s});
      } else {
        LOGGER.warning("Failure.");
      }
    } catch (IOException | CsvValidationException e) {
      long total_end_time = System.nanoTime();
      long total_duration_s = TimeUnit.NANOSECONDS.toSeconds(total_end_time - total_start_time);
      String errorType = (e instanceof IOException) ? "I/O Error" : "CSV Validation Error";
      LOGGER.log(Level.SEVERE, errorType + " during data loading from " + directoryPath + " after " + total_duration_s + " s.", e);
      success = false;
      clearData();
    }
    return success;
  }

  public Map<String, Route> getRoutesMap() { return routes_map; }
  public Map<String, Stop> getStopsMap() { return stops_map; }
  public Map<String, Trip> getTripsMap() { return trips_map; }
  public Map<String, List<StopTime>> getStopTimesMap() { return stop_times_map; }

  private void sortStopTimesBySequence() {
    LOGGER.info("Sorting stop times by sequence for all trips...");
    long start_time = System.nanoTime();
    Comparator<StopTime> sequence_comparator = Comparator.comparingInt(StopTime::getSequence);
    for (List<StopTime> schedule_list : stop_times_map.values()) {
      if (schedule_list != null) { // Only sort if needed
        schedule_list.sort(sequence_comparator);
      }
    }
    long end_time = System.nanoTime();
    long duration_s = TimeUnit.NANOSECONDS.toSeconds(end_time - start_time);
    LOGGER.log(Level.INFO, "Finished sorting stop times. Duration: {0} seconds.", duration_s);
  }

  private void parseRoutes(String route_file_path) throws IOException, CsvValidationException {
    LOGGER.log(Level.INFO, "Parsing routes from file: {0}", route_file_path);
    try (CSVReader reader = new CSVReader(new FileReader(route_file_path))) {
      String[] next_line;
      long line_index = 1;
      reader.readNext(); // Skip  header

      while ((next_line = reader.readNext()) != null) {
        line_index++;
        if (next_line.length >= 4) {
          try {
            String id = next_line[0];
            String route_short_name = next_line[1];
            String route_long_name = next_line[2];
            String route_type = next_line[3];

            if (id == null || id.isEmpty()) {
              LOGGER.log(Level.WARNING, "Skipping route line {0}: {1}: Missing ID", new Object[]{line_index, route_file_path});
              continue;
            }

            Route route = new Route(id, route_short_name, route_long_name, route_type);
            routes_map.put(id, route);
          } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing route line {0}: {1}: {2}", new Object[]{line_index, route_file_path, e.getMessage()});
            LOGGER.log(Level.FINE, "Problematic route data: {0}", String.join(",", next_line));
          }
        } else {
          LOGGER.warning("Skipping route line " + line_index + ": Expected at least 4 columns, found " + next_line.length);
        }
      }
    }
  }

  private void parseStops(String stop_file_path) throws IOException, CsvValidationException {
    LOGGER.info("Parsing stops from file: " + stop_file_path);
    try (CSVReader reader = new CSVReader(new FileReader(stop_file_path))) {
      String[] next_line;
      long line_index = 1;
      reader.readNext(); // Skip header

      while ((next_line = reader.readNext()) != null) {
        line_index++;
        if (next_line.length >= 4) {
          try {
            String id = next_line[0];
            String name = next_line[1];
            String lat_str = next_line[2];
            String long_str = next_line[3];

            if (id == null || id.isEmpty()) {
              LOGGER.warning("Skipping stop line " + line_index + ": " + stop_file_path + ": Missing ID");
              continue;
            }

            double lat = 0.0;
            double lon = 0.0;
            try {
              if (lat_str != null && !lat_str.isEmpty()) lat = Double.parseDouble(lat_str);
              if (long_str != null && !long_str.isEmpty()) lon = Double.parseDouble(long_str);
            } catch (NumberFormatException e) {
              LOGGER.warning("Could not parse lat/lon for stop_id '" + id + "' on line " + line_index + ": " + e.getMessage() + ". Using (0,0).");
            }

            Stop stop = new Stop(id, name, lat, lon);
            stops_map.put(id, stop);
          } catch (Exception e) {
            LOGGER.warning("Error processing stop line " + line_index + ": " + e.getMessage());
          }
        } else {
          LOGGER.warning("Skipping stop line " + line_index + ": Expected at least 4 fields, found " + next_line.length);
        }
      }
    }
  }
  
  @SuppressWarnings("LoggerStringConcat")
  private void parseTrips(String trip_file_path) throws IOException, CsvValidationException {
    LOGGER.info("Parsing trips from file: " + trip_file_path);
    try (CSVReader reader = new CSVReader(new FileReader(trip_file_path))) {
      String[] next_line;
      long line_index = 1;
      reader.readNext(); // Skip header
      while ((next_line = reader.readNext()) != null) {
        line_index++;
        if (next_line.length >= 2) {
          try {
            String id = next_line[0];
            String route_id = next_line[1];
            
            if (id == null || id.isEmpty() || route_id == null || route_id.isEmpty()) {
              LOGGER.warning("Skipping trip line " + line_index + ": " + trip_file_path + ": Missing ID");
              continue;
            }

            if (!routes_map.containsKey(route_id)) {
              LOGGER.warning("Routes map does not contain route id '" + route_id + "'.");
            }

            Trip trip = new Trip(id, route_id);
            trips_map.put(id, trip);
          } catch (Exception e) {
            LOGGER.warning("Error processing trip line " + line_index + ": " + e.getMessage());
          }
        } else {
          LOGGER.warning("Skipping trip line " + line_index + ": Expected at least 2 fields, found " + next_line.length);
        }
      }
    }
  }

  private void parseStopTimes(String stop_time_file_path) throws IOException, CsvValidationException {
    LOGGER.info("Parsing stop_times from file: " + stop_time_file_path);
    try (CSVReader reader = new CSVReader(new FileReader(stop_time_file_path))) {
      String[] next_line;
      long line_index = 1;
      reader.readNext(); // SKip header

      while ((next_line = reader.readNext()) != null) {
        line_index++;
        if (next_line.length >= 4) {
          try {
            String trip_id = next_line[0];
            String departure_time_str = next_line[1];
            String stop_id = next_line[2];
            String stop_sequence_str = next_line[3];

            if (trip_id == null || trip_id.isEmpty() ||
                    departure_time_str == null || departure_time_str.isEmpty() ||
                    stop_id == null || stop_id.isEmpty() ||
                    stop_sequence_str == null || stop_sequence_str.isEmpty()) {
              LOGGER.warning("Skipping stop_time line " + line_index + ": Missing required fields.");
              continue;
            }

            int stop_sequence = Integer.parseInt(stop_sequence_str);
            StopTime stop_time = new StopTime(trip_id, departure_time_str, stop_id, stop_sequence);

            List<StopTime> stop_schedule = stop_times_map.computeIfAbsent(trip_id, k -> new ArrayList<>());
            stop_schedule.add(stop_time);

          } catch (NumberFormatException e) {
            LOGGER.warning("Skipping stop_time line " + line_index + ": Invalid sequence number. " + e.getMessage());
          } catch (Exception e) {
            LOGGER.warning("Error processing stop_time line " + line_index + ": " + e.getMessage());
          }
        } else {
          LOGGER.warning("Skipping stop_time line " + line_index + ": Expected at least 4 fields, found " + next_line.length);
        }
      }
    }
  }

}
