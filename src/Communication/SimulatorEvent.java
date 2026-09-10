package Communication;

import Communication.EMSPriorityState;
import Communication.PedestrianSignalState;
import Communication.SensorState;

/**
 * EVENT:target:value
 */
public class SimulatorEvent {
    private static final String DELIMITER = ":";
    private final TLCCommand command;
    private final String target;
    private final String value;

    public SimulatorEvent ( TLCCommand command, String target, String value) {
        this.command = command;
        this.target = target;
        this.value = value;
    }

    public TLCCommand getCommand() {
        return command;
    }

    public String getTarget() {
        return target;
    }

    public String getValue() {
        return value;
    }

    // ---- Factory methods: build a typed event without hand-typing wire strings ----

    public static SimulatorEvent pedestrianSignal(String crossingId, PedestrianSignalState state) {
        return new SimulatorEvent(TLCCommand.UPDATE_PEDESTRIAN_SIGNAL, crossingId, state.name());
    }

    public static SimulatorEvent pedestrianButtonPressed(String crossingId) {
        return new SimulatorEvent(TLCCommand.PEDESTRIAN_BUTTON_PRESSED, crossingId, "PRESSED");
    }

    public static SimulatorEvent vehicleSensor(String laneId, SensorState state) {
        TLCCommand command = (state == SensorState.DETECTED) ? TLCCommand.VEHICLE_DETECTED : TLCCommand.VEHICLE_CLEARED;
        return new SimulatorEvent(command, laneId, state.name());
    }

    public static SimulatorEvent emsPriority(String direction, EMSPriorityState state) {
        TLCCommand command = (state == EMSPriorityState.REQUEST) ? TLCCommand.EMS_PRIORITY_REQUEST : TLCCommand.EMS_PRIORITY_CANCEL;
        return new SimulatorEvent(command, direction, state.name());
    }

    public static SimulatorEvent triggerFailSafe(String mode) {
        return new SimulatorEvent(TLCCommand.TRIGGER_FAIL_SAFE, "ALL", mode);
    }

    public static SimulatorEvent resumeNormal() {
        return new SimulatorEvent(TLCCommand.RESUME_NORMAL, "ALL", "NORMAL");
    }

    /**
     * Convert event into string to be sent through socket
     */
    public String toWireFormat() {
        return String.join(DELIMITER, command.name(), target, value);
    }

    /**
     *  Convert string received from socket into SimulatorEvent object
     */
    public static SimulatorEvent parse(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            throw new IllegalArgumentException(
                    "Cannot parse empty simulator event."
            );
        }
        String[] parts = rawLine.trim().split(DELIMITER, 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Malformed simulator event, expected 3 fields: " + rawLine
            );
        }
        TLCCommand command = TLCCommand.fromWire(parts[0]);
        String target = parts[1];
        String value = parts[2];
        return new SimulatorEvent(command, target, value);
    }
    @Override
    public String toString() {
        return toWireFormat();
    }
}