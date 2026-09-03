package Communication;

import Simulator.LightCol;
import Simulator.LightShape;
import Simulator.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InstructionMessageTest {

    @Test
    void roundTripPreservesAllFields() {
        InstructionMessage original = new InstructionMessage(
                TLCCommand.SET_LIGHT_STATE, 7, LightCol.Green, LightShape.Circle, Position.North);
        InstructionMessage parsed = InstructionMessage.parse(original.toWireFormat());

        assertEquals(original.getCommand(), parsed.getCommand());
        assertEquals(original.getLightID(), parsed.getLightID());
        assertEquals(original.getColor(), parsed.getColor());
        assertEquals(original.getShape(), parsed.getShape());
        assertEquals(original.getDirection(), parsed.getDirection());
    }

    @Test
    void parseRejectsEmptyLine() {
        assertThrows(IllegalArgumentException.class, () -> InstructionMessage.parse(""));
    }

    @Test
    void parseRejectsWrongFieldCount() {
        assertThrows(IllegalArgumentException.class, () -> InstructionMessage.parse("SET_LIGHT_STATE:7:GREEN"));
    }

    @Test
    void parseRejectsUnknownCommand() {
        assertThrows(IllegalArgumentException.class,
                () -> InstructionMessage.parse("NOT_REAL:7:GREEN:Circle:North"));
    }
}
