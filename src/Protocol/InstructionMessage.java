package Protocol;

import Simulator.Bearing;
import Simulator.LightCol;
import Simulator.LightShape;

/**
 * A single light-state instruction (server -> GUI). Plain data holder;
 * wire-format building/parsing lives in {@link Wire}.
 */
public final class InstructionMessage {

    private final TLCCommand command;
    private final int lightID;
    private final LightCol color;
    private final LightShape shape;
    private final Bearing direction;

    public InstructionMessage(TLCCommand command, int lightID, LightCol color, LightShape shape, Bearing direction) {
        this.command = command;
        this.lightID = lightID;
        this.color = color;
        this.shape = shape;
        this.direction = direction;
    }

    public TLCCommand getCommand() { return command; }
    public int getLightID() { return lightID; }
    public LightCol getColor() { return color; }
    public LightShape getShape() { return shape; }
    public Bearing getDirection() { return direction; }

    @Override
    public String toString() {
        return Wire.buildLightInstruction(command, lightID, color, shape, direction);
    }
}
