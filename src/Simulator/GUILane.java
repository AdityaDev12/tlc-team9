package Simulator;

import java.util.ArrayList;

public class GUILane {
    private int ID;
    private ArrayList<GUILight> myLights = new ArrayList<>();
    private ArrayList<GUISensor> mySensors = new ArrayList<>();

    public GUILane(int ID) {
        this.ID = ID;

        makeLights();
        makeSensors();
    }

    private void makeLights() {
        myLights = new ArrayList<>();
    }
    private void makeSensors() {
        mySensors = new ArrayList<>();
    }

    public LightCol getLightCol(int laneID){
        return myLights.get(laneID).getColor();
    }
    public void updateSensor(int ID, boolean isActive){
        mySensors.get(ID).setActive(isActive);
    }
    public void updateLights(int LightID, LightCol Color){
        GUILight theLight = myLights.get(LightID);
        theLight.changeColor(Color);
    }
}