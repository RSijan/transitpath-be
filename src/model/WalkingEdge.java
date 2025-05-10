package model;

public class WalkingEdge implements Edge {
  private final Stop target_stop;
  private final int duration_sec;

  public WalkingEdge(Stop target_stop, int duration_sec) {
    this.target_stop = target_stop;
    this.duration_sec = duration_sec;
  }

  @Override
  public Stop getTarget() { return target_stop; }

  public int getDurationSec() { return duration_sec; }

  @Override
  public String toString() {
    return String.format("WalkingEdge -> %s (Duration: %ds)", target_stop.getId(), duration_sec);
  }
}
