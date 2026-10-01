package Communication;

import Simulator.Position;
import Simulator.LightCol;
import Simulator.LightShape;

/**
 * A single parsed instruction
 * Wire format (pipe-delimited, 5 fields)
 * COMMAND:lightID:color:shape:direction
 */
public class InstructionMessage {

    private static final String DELIMITER = ":";

    private final TLCCommand command;
    // traffic light fields
    private int lightID;
    private LightCol color;
    private LightShape shape;
    private Position position;
    // general instruction fields
    private String target;
    private String value;

    // TrafficLight constructor
    public InstructionMessage(TLCCommand command, int lightID, LightCol color, LightShape shape, Position position) {
        this.command = command;
        this.lightID = lightID;
        this.color = color;
        this.shape = shape;
        this.position = position;
    }
    // pedestrian/general constructor
    public InstructionMessage(
            TLCCommand command,
            String target,
            String value
    ) {
        this.command = command;
        this.target = target;
        this.value = value;
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
    public Position getDirection() {
        return position;
    }
    public String getValue() {
        return value;
    }

    /** Serializes message to wire format string sent over the socket. */
    public String toWireFormat() {
        if (command == TLCCommand.SET_LIGHT_STATE) {
            return String.join(DELIMITER, command.name(), String.valueOf(lightID), color.name(), shape.name(), position.name());

        }
        return String.join(DELIMITER, command.name(), target, value);
    }

    /** Parses one raw line read off the socket back into a message object. */
    public static InstructionMessage parse(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            throw new IllegalArgumentException("Cannot parse empty instruction line");
        }
        String[] parts = rawLine.trim().split(DELIMITER);
        TLCCommand command = TLCCommand.fromWire(parts[0]);

        if (command == TLCCommand.SET_LIGHT_STATE) {
            if (parts.length != 5) {
                throw new IllegalArgumentException("InstMess: Malformed instruction, expected 5 fields: " + rawLine);
            }
            int lightID = Integer.parseInt(parts[1]);
            LightCol color = LightCol.valueOf(parts[2]);
            LightShape shape = LightShape.valueOf(parts[3]);
            Position position = Position.valueOf(parts[4]);
            return new InstructionMessage(command, lightID, color, shape, position);
        }
        if (parts.length != 3) {
            throw new IllegalArgumentException("InstMess: Malformed instruction, expected 3 fields: " + rawLine);
        }
        return new InstructionMessage(command, parts[1], parts[2]);
    }
    @Override
    public String toString() {
        return toWireFormat();
    }
}
