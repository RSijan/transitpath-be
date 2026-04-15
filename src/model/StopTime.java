package model;

/**
 * Represents when a trip stops at a specific station.
 * 
 * Each StopTime says:
 * - what trip it's part of,
 * - when it departs (in seconds),
 * - where (stop ID),
 * - and in what order (sequence).
 */
public class StopTime {
  private final String trip_id;
  private final int depart_time_seconds;
  private final String stop_id;
  private final int sequence;

  // this take a time string in the format "HH:MM:SS" and converts it to seconds.
  private static int parseTimeToSeconds(String str) {
    String[] s = str.split(":");
    int hours = Integer.parseInt(s[0]);
    int minutes = Integer.parseInt(s[1]);
    int seconds = Integer.parseInt(s[2]);
    return (hours * 3600 + minutes * 60 + seconds);
  }

  // Constructor for StopTime.
  public StopTime(String trip_id, String depart_time_str, String stop_id, int sequence) {
    this.trip_id = trip_id;
    this.depart_time_seconds = parseTimeToSeconds(depart_time_str);
    this.stop_id = stop_id;
    this.sequence = sequence;
  }

  // getter for the trip ID
  public String getTripId() {
    return trip_id;
  }

  // getter for the departure time in "HH:mm:ss" format.
  public String getDepartureTimeStr() {
    int hours = depart_time_seconds / 3600;
    int minutes = (depart_time_seconds % 3600) / 60;
    int seconds = depart_time_seconds % 60;
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }

  // getter for the departure time in seconds
  public int getDepartureTimeSeconds() {
    return depart_time_seconds;
  }

  // getter for the stop ID
  public String getStopId() {
    return stop_id;
  }

  // returns the sequence/order of the stop in the trip
  public int getSequence() {
    return sequence;
  }

  /* Compares two StopTime objects for equality.
   * Two StopTimes are equal if:
   * - they're from the same trip,
   * - have the same stop order (sequence),
   * - and leave at the same time (in seconds).
   *
   * @param o the other object to compare
   * @return true if they're equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    StopTime stopTime = (StopTime) o;
    return sequence == stopTime.sequence && depart_time_seconds == stopTime.depart_time_seconds && trip_id.equals(stopTime.trip_id);
  }

  @Override
  public int hashCode() {
    int result = trip_id.hashCode();
    result = 31 * result + sequence;
    return result;
  }

  // turns the StopTime into a readable string
  @Override
  public String toString() {
    return "StopTime{" + "tripId='" + trip_id + '\'' + ", departureTime=" + getDepartureTimeSeconds() + ", stopId='" + stop_id + '\'' + ", sequence=" + sequence + '}';
  }
}
