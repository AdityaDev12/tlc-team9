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
    private LightPattern currentPattern;

    public void setLightPattern(LightPattern pattern) {
        currentPattern = pattern;

        switch (pattern) {
            case ALL_RED:
                setAllRed(Position.North);
                setAllRed(Position.South);
                setAllRed(Position.East);
                setAllRed(Position.West);
                break;
            case NS_GREEN:
                setDirectionColor(Position.North, LightCol.Green);
                setDirectionColor(Position.South, LightCol.Green);
                setDirectionColor(Position.East, LightCol.Red);
                setDirectionColor(Position.West, LightCol.Red);
                break;
            case NS_YELLOW:
                setDirectionColor(Position.North, LightCol.Yellow);
                setDirectionColor(Position.South, LightCol.Yellow);
                setDirectionColor(Position.East, LightCol.Red);
                setDirectionColor(Position.West, LightCol.Red);
                break;
            case NS_ARROW_GREEN:
                setNorthSouthArrowPattern(LightCol.Green);
                setDirectionColor(Position.East, LightCol.Red);
                setDirectionColor(Position.West, LightCol.Red);
                break;
            case NS_ARROW_YELLOW:
                setNorthSouthArrowPattern(LightCol.Yellow);
                setDirectionColor(Position.East, LightCol.Red);
                setDirectionColor(Position.West, LightCol.Red);
                break;
            case EW_GREEN:
                setDirectionColor(Position.North, LightCol.Red);
                setDirectionColor(Position.South, LightCol.Red);
                setDirectionColor(Position.East, LightCol.Green);
                setDirectionColor(Position.West, LightCol.Green);
                break;
            case EW_YELLOW:
                setDirectionColor(Position.North, LightCol.Red);
                setDirectionColor(Position.South, LightCol.Red);
                setDirectionColor(Position.East, LightCol.Yellow);
                setDirectionColor(Position.West, LightCol.Yellow);
                break;
            case EW_ARROW_GREEN:
                setEastWestArrowPattern(LightCol.Green);
                setDirectionColor(Position.North, LightCol.Red);
                setDirectionColor(Position.South, LightCol.Red);
                break;
            case EW_ARROW_YELLOW:
                setEastWestArrowPattern(LightCol.Yellow);
                setDirectionColor(Position.North, LightCol.Red);
                setDirectionColor(Position.South, LightCol.Red);
                break;
            default:
                throw new IllegalArgumentException("TrafficLights: Unknown light pattern: " + pattern);
        }
    }

    private void setAllRed(Position direction) {
        for (int lightID = 0; lightID < 3; lightID++) {
            InstructionMessage message = new InstructionMessage(
                    TLCCommand.SET_LIGHT_STATE,
                    lightID,
                    LightCol.Red,
                    LightShape.Square,
                    direction
            );
            mux.sendInstruction(message);
        }
    }

    // all three lights for one direction set to same color
    private void setDirectionColor(Position direction, LightCol color) {
        for (int lightID = 0; lightID <3; lightID++) {
            InstructionMessage message = new InstructionMessage(
                    TLCCommand.SET_LIGHT_STATE,
                    lightID,
                    color,
                    LightShape.Square,
                    direction
            );
            mux.sendInstruction(message);
        }
    }

    private void setNorthSouthArrowPattern(LightCol color) {
        setDirectionArrowPattern(Position.North, color);
        setDirectionArrowPattern(Position.South, color);
    }
    private void setEastWestArrowPattern(LightCol color) {
        setDirectionArrowPattern(Position.East, color);
        setDirectionArrowPattern(Position.West, color);
    }
    private void setDirectionArrowPattern(Position direction, LightCol color) {
        for (int lightID = 0; lightID < 3; lightID++) {
            LightShape shape = getShape(direction,lightID);
            // left turn lane gets color, through lanes stay red
            LightCol lightColor;
            if (shape == LightShape.LeftArrow) {
                lightColor = color;
            } else {
                lightColor = LightCol.Red;
            }
            InstructionMessage message = new InstructionMessage(
                    TLCCommand.SET_LIGHT_STATE,
                    lightID,
                    lightColor,
                    shape,
                    direction
            );
            mux.sendInstruction(message);
        }
    }

    private LightShape getShape(Position direction, int lightID) {
        switch (direction) {
            case North:
            case East:
                return switch (lightID) {
                    case 0 -> LightShape.LeftArrow;
                    case 1, 2 -> LightShape.Square;
                    default -> throw new IllegalArgumentException("TrafficLights: Invalid light ID: " + lightID);
                };
            case South:
            case West:
                return switch (lightID) {
                    case 0, 1 -> LightShape.Square;
                    case 2 -> LightShape.LeftArrow;
                    default -> throw new IllegalArgumentException("TrafficLights: Invalid light ID: " + lightID);
                };
            default:
                throw new IllegalArgumentException("TrafficLights: Unknown direction: " + direction);
        }
    }

    public LightPattern getCurrentPattern() {
        return currentPattern;
    }
}
