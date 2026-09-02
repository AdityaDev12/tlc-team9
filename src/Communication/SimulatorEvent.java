package Communication;
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

