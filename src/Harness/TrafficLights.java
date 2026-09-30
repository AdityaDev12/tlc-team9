package Harness;

import Communication.InstructionMessage;
import Communication.TLCCommand;
import Simulator.LightCol;
import Simulator.LightShape;
import Simulator.Position;

public class TrafficLights {
    private final Mux mux;
    public TrafficLights(Mux mux) {
        this.mux = mux;
    }

    public void setLightPattern(LightPattern pattern) {
        switch (pattern) {
            case ALL_RED:
                setAllRed();
                break;
            case NS_GREEN:
                setNorthSouth (
                        LightCol.Green, LightShape.Square
                );
                setEastWest(
                        LightCol.Red,LightShape.Square
                );
                break;
            case NS_ARROW_GREEN:
                setNorthSouth(
                        LightCol.Green, LightShape.LeftArrow
                );
                setEastWest(
                        LightCol.Red, LightShape.Square
                );
                break;
            case NS_ARROW_YELLOW:
                setNorthSouth(
                        LightCol.Yellow, LightShape.LeftArrow
                );
                setEastWest(
                        LightCol.Red, LightShape.Square
                );
                break;
            case NS_YELLOW:
                setNorthSouth (
                        LightCol.Yellow, LightShape.Square
                );
                setEastWest(
                        LightCol.Red,LightShape.Square
                );
                break;
            case EW_GREEN:
                setNorthSouth (
                        LightCol.Red, LightShape.Square
                );
                setEastWest(
                        LightCol.Green,LightShape.Square
                );
                break;
            case EW_ARROW_GREEN:
                setNorthSouth(
                        LightCol.Red, LightShape.Square
                );
                setEastWest(
                        LightCol.Green, LightShape.LeftArrow
                );
                break;
            case EW_ARROW_YELLOW:
                setNorthSouth(
                        LightCol.Red, LightShape.Square
                );
                setEastWest(
                        LightCol.Yellow, LightShape.LeftArrow
                );
                break;
            case EW_YELLOW:
                setNorthSouth (
                        LightCol.Red, LightShape.Square
                );
                setEastWest(
                        LightCol.Yellow,LightShape.Square
                );
                break;
            default:
                throw new IllegalArgumentException("TrafficLights: Unknown light pattern: " + pattern);
        }
    }
    private void setAllRed() {
        setNorthSouth(
                LightCol.Red, LightShape.Square
        );
        setEastWest(
                LightCol.Red, LightShape.Square
        );
    }
    private void setNorthSouth(LightCol color, LightShape shape) {
        setDirection(Position.North, color, shape);
        setDirection(Position.South, color, shape);
    }
    private void setEastWest(LightCol color, LightShape shape) {
        setDirection(Position.East, color, shape);
        setDirection(Position.West, color, shape);
    }
    private void setDirection (Position position, LightCol color, LightShape shape){
        int lightID = getLightID(position, shape);
        InstructionMessage message = new InstructionMessage(
                TLCCommand.SET_LIGHT_STATE,
                lightID,
                color,
                shape,
                position
        );
        mux.sendInstruction(message);
    }
    private int getLightID(Position position, LightShape shape) {
        throw new UnsupportedOperationException(
                "TrafficLights: lightID mapping not configured."
        );
    }
}
