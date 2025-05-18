package utils;

import model.Stop;

/**
 * This class provides utility methods to calculate estimated transit and walking durations
 * between two stops based on their geographical coordinates.
 */
public class TransitDurationCalculator {

  private static final int EARTH_RADIUS_METERS = 6371000; // Radius of the Earth in meters
  private static final double AVERAGE_WALKING_SPEED = 1.3; // 1.3 m/s
  private static final double MAX_TRANSPORT_SPEED =  100*1000/3600; // 100 km/h
  private static final int MAX_WALKING_DURATION_SEC = 900; // 15 minutes
  private static final int MIN_WALKING_DURATION_SEC = 30; // 30 seconds

  private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    // Convert everything to radians
    double lat1Rad = Math.toRadians(lat1);
    double lon1Rad = Math.toRadians(lon1);
    double lat2Rad = Math.toRadians(lat2);
    double lon2Rad = Math.toRadians(lon2);

    double deltaLat = lat2Rad - lat1Rad;
    double deltaLon = lon2Rad - lon1Rad;

    // Use haversine formula
    double a = Math.pow(Math.sin(deltaLat / 2), 2) +
               Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(deltaLon / 2), 2);
    double c = 2 * Math.asin(Math.sqrt(a));

    // Return distance
    return EARTH_RADIUS_METERS * c;
  }
  /** 
   * Calculates the estimated duration between two stops, useful for A*
   * @param stopA The first stop
   * @param stopB The second stop
   * @return The estimated duration in seconds
   */
  public static int calculateTotalDuration(Stop stopA, Stop stopB) {
    double distance = calculateDistance(stopA.getLat(), stopA.getLon(), stopB.getLat(), stopB.getLon());
    return (int) Math.ceil((distance / MAX_TRANSPORT_SPEED));
  }

  /**
   * Calculates the estimated walking duration between two stops
   * @param stopA The starting stop
   * @param stopB The destination stop
   * @return The estimated walking duration in seconds
   */
  public static int calculateWalkDuration(Stop stopA, Stop stopB) {
    double distance = calculateDistance(stopA.getLat(), stopA.getLon(), stopB.getLat(), stopB.getLon());
    // Raw duration just distance/speed
    double rawDurationSeconds = distance / AVERAGE_WALKING_SPEED;

    // Apply minimum duration (cuz a very short walk doesn't make much sense)
    int estimatedDuration = (int) Math.round(rawDurationSeconds); // Round to nearest second
    estimatedDuration = Math.max(estimatedDuration, MIN_WALKING_DURATION_SEC);

    // Apply maximum duration threshold (walks longer than 15 minutes are too long)
    if (estimatedDuration > MAX_WALKING_DURATION_SEC) {
      return Integer.MAX_VALUE;
    }

    return estimatedDuration;
  }
}