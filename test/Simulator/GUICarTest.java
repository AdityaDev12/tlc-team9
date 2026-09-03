package Simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GUICarTest {

    @Test
    void carCannotMoveUntilLightIsGreenAndDistanceReached() {
        GUILane lane = new GUILane(0, Bearing.North);
        GUICar car = new GUICar(0, lane, Bearing.North, 1, null, false);

        assertTrue(car.canMove()); // default true before it ever checks the light

        car.addDistance(60); // crosses the 50-distance threshold
        assertFalse(car.canMove()); // lane is still red by default
    }

    @Test
    void carMovesOnceLaneIsGreen() {
        GUILane lane = new GUILane(0, Bearing.North);
        lane.updateLights(0, LightCol.Green);
        GUICar car = new GUICar(0, lane, Bearing.North, 1, null, false);

        car.addDistance(60);
        assertTrue(car.canMove());
    }

    @Test
    void carStaysCommittedAfterLightTurnsRedAgain() {
        // this is the exact bug we hit: a car that already got a green
        // should NOT be stopped again by a later red, once it's committed
        GUILane lane = new GUILane(0, Bearing.North);
        lane.updateLights(0, LightCol.Green);
        GUICar car = new GUICar(0, lane, Bearing.North, 1, null, false);

        car.addDistance(60);
        assertTrue(car.canMove());

        lane.updateLights(0, LightCol.Red); // light changes again
        car.addDistance(10); // car keeps moving, distance keeps growing

        assertTrue(car.canMove()); // must stay true - already committed
    }

    @Test
    void sensorActivatesAtDistanceThreshold() {
        GUILane lane = new GUILane(0, Bearing.North);
        GUICar car = new GUICar(0, lane, Bearing.North, 1, null, false);

        assertFalse(car.isSensorActive());
        car.addDistance(60);
        assertTrue(car.isSensorActive());
    }

    @Test
    void leaveSensorDeactivatesWithoutThrowing() {
        GUILane lane = new GUILane(0, Bearing.North);
        GUICar car = new GUICar(0, lane, Bearing.North, 2, null, false); // lane 2, not index 0

        car.addDistance(60);
        assertTrue(car.isSensorActive());

        assertDoesNotThrow(car::leaveSensor); // this exact call used to crash with the wrong index
        assertFalse(car.isSensorActive());
    }
}