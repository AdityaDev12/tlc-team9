package Communication;

/**
 * State of a pedestrian crossing signal.
 * Carried as the `value` field of an UPDATE_PEDESTRIAN_SIGNAL SimulatorEvent.
 */
public enum PedestrianSignalState {
    WALK,
    WAIT
}