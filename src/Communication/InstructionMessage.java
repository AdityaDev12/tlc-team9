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

    private static String DELIMITER = ":";

    private TLCCommand command;
    private int lightID;
    private LightCol color;
    private LightShape shape;
    private Position position;

    public InstructionMessage(TLCCommand command, int lightID, LightCol color, LightShape shape, Position position) {
        this.command = command;
        this.lightID = lightID;
        this.color = color;
        this.shape = shape;
        this.position = position;
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

    /** Serializes this message to the wire format string sent over the socket. */
    public String toWireFormat() {
        return String.join(DELIMITER, command.name(), String.valueOf(lightID), color.name(), shape.name(), position.name());
    }

    /** Parses one raw line read off the socket back into a message object. */
    public static InstructionMessage parse(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            throw new IllegalArgumentException("Cannot parse empty instruction line");
        }
        String[] parts = rawLine.trim().split(DELIMITER);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Malformed instruction, expected 5 fields: " + rawLine);
        }
        TLCCommand command = TLCCommand.fromWire(parts[0]);
        int lightID = Integer.parseInt(parts[1]);
        LightCol color = LightCol.valueOf(parts[2]);
        LightShape shape = LightShape.valueOf(parts[3]);
        Position position = Position.valueOf(parts[4]);
        return new InstructionMessage(command, lightID, color, shape, position);
    }

    @Override
    public String toString() {
        return toWireFormat();
    }
}
