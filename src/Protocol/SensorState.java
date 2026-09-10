package Protocol;

/**
 * State reported by a vehicle sensor embedded in a lane.
 * Carried as the `value` field of a VEHICLE_DETECTED / VEHICLE_CLEARED SimulatorEvent.
 */
public enum SensorState {
    DETECTED,
    CLEARED
}