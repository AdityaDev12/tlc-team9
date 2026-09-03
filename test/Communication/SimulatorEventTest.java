package Communication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimulatorEventTest {

    @Test
    void roundTripPreservesAllFields() {
        SimulatorEvent original = new SimulatorEvent(TLCCommand.VEHICLE_DETECTED, "3", "DETECTED");
        SimulatorEvent parsed = SimulatorEvent.parse(original.toWireFormat());

        assertEquals(original.getCommand(), parsed.getCommand());
        assertEquals(original.getTarget(), parsed.getTarget());
        assertEquals(original.getValue(), parsed.getValue());
    }

    @Test
    void parseRejectsEmptyLine() {
        assertThrows(IllegalArgumentException.class, () -> SimulatorEvent.parse(""));
    }

    @Test
    void parseRejectsUnknownCommand() {
        assertThrows(IllegalArgumentException.class, () -> SimulatorEvent.parse("NOT_REAL:3:DETECTED"));
    }
}
