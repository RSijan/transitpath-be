package model;

import com.univocity.parsers.csv.Csv;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {

  private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

  private final Map<String, Route>   routes_map_      = new ConcurrentHashMap<>();
  private final Map<String, Stop>    stops_map_      = new ConcurrentHashMap<>();
  private final Map<String, Trip>    trips_map_      = new ConcurrentHashMap<>();
  private final Map<String, List<StopTime>> stop_times_map_ = new ConcurrentHashMap<>();

  private final int AVERAGE_STOP_SEQUENCE_LENGTH = 28; // Calculated from the GTFS data


  public void clearData() {
    routes_map_.clear();
    stops_map_.clear();
    trips_map_.clear();
    stop_times_map_.clear();
  }

  public boolean loadData(List<String> directoryPath) {
    long total_start_time = System.nanoTime();
    clearData();

    AtomicBoolean success = new AtomicBoolean(true);

    directoryPath.parallelStream().forEach(path -> {
      Path dir = Paths.get(path);
      if (!Files.isDirectory(dir)) {
        LOGGER.log(Level.SEVERE, "Path {0} is not a directory.", path);
        success.set(false);
        return;
      }
      try {
        parseRoutes(Paths.get(path, "routes.csv").toString());
        parseStops (Paths.get(path, "stops.csv" ).toString());
        parseTrips (Paths.get(path, "trips.csv" ).toString());
        parseStopTimes(Paths.get(path, "stop_times.csv").toString());
      } catch (IOException e) {
        String errorType = (e instanceof IOException) ? "I/O Error" : "CSV Validation Error";
        LOGGER.log(Level.SEVERE, errorType + " loading from " + path, e);
        success.set(false);
      }
    });

    if (success.get()) {
      sortStopTimesBySequence();
      double secs = (System.nanoTime() - total_start_time) / 1e9;
      System.out.printf("Finished parsing transit data successfully. Total time taken in %.3f s.%n", secs);
    } else {
      LOGGER.warning("One or more directories failed to load.");
      clearData();
    }
    return success.get();
  }

  public Map<String, Route> getRoutesMap() { return routes_map_; }
  public Map<String, Stop> getStopsMap() { return stops_map_; }
  public Map<String, Trip> getTripsMap() { return trips_map_; }
  public Map<String, List<StopTime>> getStopTimesMap() { return stop_times_map_; }

  private void sortStopTimesBySequence() {
    Comparator<StopTime> sequence_comparator = Comparator.comparingInt(StopTime::getSequence);
    for (List<StopTime> schedule_list : stop_times_map_.values()) {
      if (schedule_list != null) { // Only sort if needed
        schedule_list.sort(sequence_comparator);
      }
    }
  }

  private void parseRoutes(String route_file_path) throws IOException {
    CsvParser parser = createParser();
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(route_file_path))) {
      parser.beginParsing(reader);

      String[] next_line;
      long line_index = 1;

      while ((next_line = parser.parseNext()) != null) {
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
            routes_map_.put(id, route);
          } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing route line {0}: {1}: {2}", new Object[]{line_index, route_file_path, e.getMessage()});
            LOGGER.log(Level.FINE, "Problematic route data: {0}", String.join(",", next_line));
          }
        } else {
          LOGGER.log(Level.WARNING, "Skipping route line {0}: Expected at least 4 columns, found {1}", new Object[]{line_index, next_line.length});
        }
      }
      parser.stopParsing();
    }
  }

  private void parseStops(String stop_file_path) throws IOException {
    CsvParser parser = createParser();
    try (var reader = Files.newBufferedReader(Paths.get(stop_file_path))) {
      parser.beginParsing(reader);

      String[] next_line;
      long line_index = 1;

      while ((next_line = parser.parseNext()) != null) {
        line_index++;
        if (next_line.length >= 4) {
          try {
            String id = next_line[0];
            String name = next_line[1];
            String lat_str = next_line[2];
            String long_str = next_line[3];

            if (id == null || id.isEmpty()) {
              LOGGER.log(Level.WARNING, "Skipping stop line {0}: {1}: Missing ID", new Object[]{line_index, stop_file_path});
              continue;
            }

            double lat = 0.0;
            double lon = 0.0;
            try {
              if (lat_str != null && !lat_str.isEmpty()) lat = Double.parseDouble(lat_str);
              if (long_str != null && !long_str.isEmpty()) lon = Double.parseDouble(long_str);
            } catch (NumberFormatException e) {
              LOGGER.log(Level.WARNING, "Could not parse lat/lon for stop_id ''{0}'' on line {1}: {2}. Using (0,0).", new Object[]{id, line_index, e.getMessage()});
            }

            Stop stop = new Stop(id, name, lat, lon);
            stops_map_.put(id, stop);
          } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing stop line {0}: {1}", new Object[]{line_index, e.getMessage()});
          }
        } else {
          LOGGER.log(Level.WARNING, "Skipping stop line {0}: Expected at least 4 fields, found {1}", new Object[]{line_index, next_line.length});
        }
      }
      parser.stopParsing();
    }
  }
  
  private void parseTrips(String trip_file_path) throws IOException {
    CsvParser parser = createParser();
    try (var reader = Files.newBufferedReader(Paths.get(trip_file_path))) {
      parser.beginParsing(reader);

      String[] next_line;
      long line_index = 1;

      while ((next_line = parser.parseNext()) != null) {
        line_index++;
        if (next_line.length >= 2) {
          try {
            String id = next_line[0];
            String route_id = next_line[1];
            
            if (id == null || id.isEmpty() || route_id == null || route_id.isEmpty()) {
              LOGGER.log(Level.WARNING, "Skipping trip line {0}: {1}: Missing ID", new Object[]{line_index, trip_file_path});
              continue;
            }

            if (!routes_map_.containsKey(route_id)) {
              LOGGER.log(Level.WARNING, "Routes map does not contain route id ''{0}''.", route_id);
            }

            Trip trip = new Trip(id, route_id);
            trips_map_.put(id, trip);
          } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing trip line {0}: {1}", new Object[]{line_index, e.getMessage()});
          }
        } else {
          LOGGER.log(Level.WARNING, "Skipping trip line {0}: Expected at least 2 fields, found {1}", new Object[]{line_index, next_line.length});
        }
      }
      parser.stopParsing();
    }
  }

  private void parseStopTimes(String stop_time_file_path) throws IOException {
    CsvParser parser = createParser();
    try (var reader = Files.newBufferedReader(Paths.get(stop_time_file_path))) {
      parser.beginParsing(reader);

      String[] next_line;
      long line_index = 1;

      while ((next_line = parser.parseNext()) != null) {
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
              LOGGER.log(Level.WARNING, "Skipping stop_time line {0}: Missing required fields.", line_index);
              continue;
            }

            int stop_sequence = Integer.parseInt(stop_sequence_str);
            StopTime stop_time = new StopTime(trip_id, departure_time_str, stop_id, stop_sequence);

            List<StopTime> stop_schedule = stop_times_map_.computeIfAbsent(trip_id, _ -> new ArrayList<>(AVERAGE_STOP_SEQUENCE_LENGTH));
            stop_schedule.add(stop_time);

          } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping stop_time line {0}: Invalid sequence number. {1}", new Object[]{line_index, e.getMessage()});
          } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing stop_time line {0}: {1}", new Object[]{line_index, e.getMessage()});
          }
        } else {
          LOGGER.log(Level.WARNING, "Skipping stop_time line {0}: Expected at least 4 fields, found {1}", new Object[]{line_index, next_line.length});
        }
      }
      parser.stopParsing();
    }
  }

  private CsvParser createParser() {
    CsvParserSettings settings = new CsvParserSettings();
    settings.setHeaderExtractionEnabled(true);
    settings.setLineSeparatorDetectionEnabled(true);
    settings.setMaxColumns(10_000);  // high‐water mark for GTFS
    return new CsvParser(settings);
  }

}
