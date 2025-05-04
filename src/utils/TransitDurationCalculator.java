package utils;

import java.util.logging.Logger;
import model.Stop;

public class TransitDurationCalculator {

  private static final int EARTH_RADIUS_METERS = 6371000;
  private static final double AVERAGE_WALKING_SPEED = 1.42;
  private static final double MAX_TRANSPORT_SPEED =  100*1000/3600; // 200 km/h
  private static final int MAX_WALKING_DURATION_SEC = 1200;
  private static final int MIN_WALKING_DURATION_SEC = 60;

  private static final Logger LOGGER = Logger.getLogger(TransitDurationCalculator.class.getName());

  private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
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

  public static int calculateTotalDuration(Stop stopA, Stop stopB) {
    double distance = calculateDistance(stopA.getLat(), stopA.getLon(), stopB.getLat(), stopB.getLon());
    return (int) Math.ceil((distance / MAX_TRANSPORT_SPEED));
  }

  public static int calculateTransitDuration(Stop stopA, Stop stopB, String route_type) {
    double distance = calculateDistance(stopA.getLat(), stopA.getLon(), stopB.getLat(), stopB.getLon());
    double averageTransportSpeed = getAverageMaxSpeed(route_type);
    return (int) (distance / averageTransportSpeed);
  }

   public static int calculateWalkDuration(Stop stopA, Stop stopB) {
      double distance = calculateDistance(stopA.getLat(), stopA.getLon(), stopB.getLat(), stopB.getLon());
      // Raw duration just distance/speed
      double rawDurationSeconds = distance / AVERAGE_WALKING_SPEED;

      // Apply minimum duration (cuz a 10 sec walk doesn't make much sense)
     int estimatedDuration = (int) Math.round(rawDurationSeconds); // Round to nearest second
     estimatedDuration = Math.max(estimatedDuration, MIN_WALKING_DURATION_SEC);

     // Apply maximum duration threshold (walks longer than 15 minutes are too long)
     if (estimatedDuration > MAX_WALKING_DURATION_SEC) {
       return Integer.MAX_VALUE;
     }

      return estimatedDuration;
   }

   private static double getAverageMaxSpeed(String route_type) {
     if (route_type.equalsIgnoreCase("bus")) {
       return 50.0 * 1000 / 3600;
     } else if (route_type.equalsIgnoreCase("tram")) {
       return 30.0 * 1000 / 3600;
     } else if (route_type.equalsIgnoreCase("metro")) {
       return 45.0 * 1000 / 3600;
     } else if (route_type.equalsIgnoreCase("train")) {
       return 150.0 * 1000 / 3600;
     } else {
       LOGGER.severe("Wrong route type received");
       return 0;
     }
   }
}