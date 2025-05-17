package model;
 /**
 * Represents a walking connection between two stops
 * 
 * this contains the target stop and the duration of the walk
 */

public class WalkingEdge implements Edge {
  private final Stop target_stop;
  private final int duration_sec;

  // Constructor for WalkingEdge.
  public WalkingEdge(Stop target_stop, int duration_sec) {
    this.target_stop = target_stop;
    this.duration_sec = duration_sec;
  }

  @Override
  // Getter for the target stop.
  public Stop getTarget() { return target_stop; }

  // This method returns the duration of the walking edge in seconds.
  public int getDurationSec() { return duration_sec; }

  // turns the WalkingEdge into a readable string
  @Override
  public String toString() {
    return String.format("WalkingEdge -> %s (Duration: %ds)", target_stop.getId(), duration_sec);
  }
}
