package Communication;

/**
 * State of an EMS priority request from the antenna receiver.
 * Carried as the `value` field of an EMS_PRIORITY_REQUEST / EMS_PRIORITY_CANCEL SimulatorEvent.
 */
public enum EMSPriorityState {
    REQUEST,
    CANCEL
}