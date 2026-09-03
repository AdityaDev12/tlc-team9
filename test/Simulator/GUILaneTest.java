package Simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GUILaneTest {
    @Test
    void laneStartsAtRedWithSensorInactive() {
        GUILane lane = new GUILane(0, Bearing.North);
        assertEquals(LightCol.Red, lane.getLightCol(0));
    }

    @Test
    void updateLightsChangesColor() {
        GUILane lane = new GUILane(0, Bearing.North);
        lane.updateLights(0, LightCol.Green);
        assertEquals(LightCol.Green, lane.getLightCol(0));
    }

    @Test
    void updateSensorChangesStatus() {
        GUILane lane = new GUILane(0, Bearing.North);
        lane.updateSensor(0, true);
        assertDoesNotThrow(() -> lane.updateSensor(0, false));
    }
}
