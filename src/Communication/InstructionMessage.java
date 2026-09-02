package Communication;

import Simulator.Bearing;
import Simulator.LightCol;
import Simulator.LightShape;

/**
 * A single parsed instruction
 * Wire format (pipe-delimited, 5 fields)
 * COMMAND:lightID:color:shape:direction
 */
public final class InstructionMessage {

    private static final String DELIMITER = ":";

    private final TLCCommand command;
    private final int lightID;
    private final LightCol color;
    private final LightShape shape;
    private final Bearing direction;
    //private final String target;
    //private final String value;
    //private final long timestampMillis;

    //public InstructionMessage(TLCCommand command, String target, String value, long timestampMillis) {
    public InstructionMessage(TLCCommand command, int lightID, LightCol color, LightShape shape, Bearing direction) {
        this.command = command;
        this.lightID = lightID;
        this.color = color;
        this.shape = shape;
        this.direction = direction;
        //this.target = target;
        //this.value = value;
        //this.timestampMillis = timestampMillis;
    }

    public TLCCommand getCommand() { return command; }
    public int getLightID() {
        return lightID;
    }
    public LightCol getColor() {
        return color;
    }
    public LightShape getShape() {
        return shape;
    }
    public Bearing getDirection() {
        return direction;
    }
    //public String getTarget() { return target; }
    //public String getValue() { return value; }
    //public long getTimestampMillis() { return timestampMillis; }

    /** Convenience factory that stamps the current time — use this when sending.
    public static InstructionMessage of(TLCCommand command, String target, String value) {
        return new InstructionMessage(command, target, value, Instant.now().toEpochMilli());
    }

    /** Serializes this message to the wire format string sent over the socket. */
    public String toWireFormat() {
        return String.join(DELIMITER, command.name(), String.valueOf(lightID), color.name(), shape.name(), direction.name());
        //return String.join(DELIMITER, command.name(), target, value, String.valueOf(timestampMillis));
    }

    /** Parses one raw line read off the socket back into a message object. */
    public static InstructionMessage parse(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            throw new IllegalArgumentException("Cannot parse empty instruction line");
        }
        // limit=4 so a value that legitimately contains no extra colons is fine;
        // if a value ever needs a colon, this still parses because TARGET/VALUE never do.
        String[] parts = rawLine.trim().split(DELIMITER);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Malformed instruction, expected 5 fields: " + rawLine);
        }
        TLCCommand command = TLCCommand.fromWire(parts[0]);
        int lightID = Integer.parseInt(parts[1]);
        LightCol color = LightCol.valueOf(parts[2]);
        LightShape shape = LightShape.valueOf(parts[3]);
        Bearing direction = Bearing.valueOf(parts[4]);
        //String target = parts[1];
        //String value = parts[2];
        //long timestamp;
        //try {
        //    timestamp = Long.parseLong(parts[3]);
        //} catch (NumberFormatException e) {
        //    throw new IllegalArgumentException("Invalid timestamp field in: " + rawLine, e);
        //}
        //return new InstructionMessage(command, target, value, timestamp);
        return new InstructionMessage(command, lightID, color, shape, direction);
    }

    @Override
    public String toString() {
        return toWireFormat();
    }
}
