package Simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GUISensorTest {
    @Test
    void newSensorDefaultsToInactive() {
        assertFalse(new GUISensor(0).getStatus());
    }

    @Test
    void setActiveUpdatesState() {
        GUISensor sensor = new GUISensor(0);
        sensor.setActive(true);
        assertTrue(sensor.getStatus());
    }
}
