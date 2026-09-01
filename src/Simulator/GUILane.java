package Simulator;

import java.util.ArrayList;

public class GUILane {
    private int ID;
    private ArrayList<GUILight> myLights = new ArrayList<>();
    private GUISensor mySensor;

    public GUILane(int ID) {
        this.ID = ID;

        makeLights();
    }

    private void makeLights() {
        LightShape shape = switch (laneNumber) {
            case 0 -> LightShape.LeftArrow;
            case 2 -> LightShape.RightArrow;
            default -> LightShape.Circle;
        };
        myLights.add(new GUILight(0, shape, direction));
        myLights.add(new GUILight(1, shape, direction));
        myLights.add(new GUILight(2, shape, direction));
    }
    public void makeSensor(GUISensor mySensor) {
        this.mySensor = mySensor;
    }
    public void updateLights(int LightID, LightCol Color){
        GUILight theLight = myLights.get(LightID);
        theLight.changeColor(Color);
    }
}