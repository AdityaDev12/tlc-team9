package Simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GUILightTest {
    @Test
    void newLightDefaultsToRed() {
        assertEquals(LightCol.Red, new GUILight(0, LightShape.Circle, Bearing.North).getColor());
    }

    @Test
    void changeColorUpdatesState() {
        GUILight light = new GUILight(0, LightShape.Circle, Bearing.North);
        light.changeColor(LightCol.Green);
        assertEquals(LightCol.Green, light.getColor());
    }
}
