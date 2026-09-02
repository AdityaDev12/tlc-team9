package Protocol;

import Simulator.Bearing;
import Simulator.LightCol;
import Simulator.LightShape;

/**
 * Wire-format building and parsing for the TLC protocol. ":" delimited.
 *
 * Light instructions (5 fields): COMMAND:LIGHT_ID:COLOR:SHAPE:DIRECTION
 * Events (3 fields):             COMMAND:TARGET:VALUE
 */
public final class Protocol {

    private static final String DELIMITER = ":";

    private Protocol() {
    }

    /** Serializes a light-state instruction to its wire format. */
    public static String buildLightInstruction(TLCCommand command, int lightID, LightCol color,
                                                 LightShape shape, Bearing direction) {
        return String.join(DELIMITER,
                command.name(),
                String.valueOf(lightID),
                color.name(),
                shape.name(),
                direction.name());
    }

    /** Parses one raw line into an InstructionMessage. Expects exactly 5 colon-separated fields. */
    public static InstructionMessage parseLightInstruction(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            throw new IllegalArgumentException("Cannot parse empty light instruction line");
        }
        String[] parts = rawLine.trim().split(DELIMITER, -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Malformed light instruction, expected 5 fields: " + rawLine);
        }
        TLCCommand command = TLCCommand.fromWire(parts[0]);
        int lightID;
        try {
            lightID = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid lightID field in: " + rawLine, e);
        }
        LightCol color = parseEnum(LightCol.class, parts[2], rawLine);
        LightShape shape = parseEnum(LightShape.class, parts[3], rawLine);
        Bearing direction = parseEnum(Bearing.class, parts[4], rawLine);
        return new InstructionMessage(command, lightID, color, shape, direction);
    }

    /** Serializes an event to its wire format. */
    public static String buildEvent(TLCCommand command, String target, String value) {
        return String.join(DELIMITER, command.name(), target, value);
    }

    /** Parses one raw line into a SimulatorEvent. Expects exactly 3 colon-separated fields. */
    public static SimulatorEvent parseEvent(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            throw new IllegalArgumentException("Cannot parse empty event line");
        }
        String[] parts = rawLine.trim().split(DELIMITER, -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Malformed event, expected 3 fields: " + rawLine);
        }
        TLCCommand command = TLCCommand.fromWire(parts[0]);
        String target = parts[1];
        String value = parts[2];
        return new SimulatorEvent(command, target, value);
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> enumType, String token, String rawLine) {
        try {
            return Enum.valueOf(enumType, token.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unrecognized " + enumType.getSimpleName() + " value in: " + rawLine, e);
        }
    }
}
