package Harness;

import Communication.InstructionMessage;
import Communication.TLCCommand;
import Simulator.LightCol;
import Simulator.LightShape;
import Simulator.Position;

/**
 * Each approach (North/South/East/West) has 3 lights  mirroring
 * the Simulator's lane layout.
 */
public class TrafficLights {
    private static final int LANES_PER_DIRECTION = 3;

    private Mux mux;
    public TrafficLights(Mux mux) {
        this.mux = mux;
    }

    public void setLightPattern(LightPattern pattern) {
        for (Position position : Position.values()) {
            LightCol color = colorFor(pattern, position);
            for (int lightID = 0; lightID < LANES_PER_DIRECTION; lightID++) {
                LightShape shape = shapeFor(position, lightID);
                mux.sendInstruction(new InstructionMessage(TLCCommand.SET_LIGHT_STATE, lightID, color, shape, position));
            }
        }
    }

    // Determines the color a given approach should show for this pattern.
    private LightCol colorFor(LightPattern pattern, Position position) {
        boolean northSouth = position == Position.North || position == Position.South;
        return switch (pattern) {
            case ALL_RED -> LightCol.Red;
            case NS_GREEN -> northSouth ? LightCol.Green : LightCol.Red;
            case NS_YELLOW -> northSouth ? LightCol.Yellow : LightCol.Red;
            case EW_GREEN -> northSouth ? LightCol.Red : LightCol.Green;
            case EW_YELLOW -> northSouth ? LightCol.Red : LightCol.Yellow;
        };
    }

    // Maps a lightID to the shape it physically displays for a given approach.
    private LightShape shapeFor(Position position, int lightID) {
        boolean northOrEast = position == Position.North || position == Position.East;
        return switch (lightID) {
            case 0 -> northOrEast ? LightShape.LeftArrow : LightShape.RightArrow;
            case 2 -> northOrEast ? LightShape.RightArrow : LightShape.LeftArrow;
            default -> LightShape.Square;
        };
    }
}