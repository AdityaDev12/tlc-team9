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

    public void makeLights() {
        myLights = new ArrayList<>();
    }

    public void makeSensor(GUISensor mySensor) {
        this.mySensor = mySensor;
    }
    public void updateLights(int LightID, LightCol Color){
        GUILight theLight = myLights.get(LightID);
        theLight.changeColor(Color);
    }
}