package Simulator;

import java.util.ArrayList;

public class GUILane {
    private int ID;
    private Bearing bearing;
    private ArrayList<GUILight> myLights = new ArrayList<>();
    private ArrayList<GUISensor> mySensors = new ArrayList<>();


    public GUILane(int ID, Bearing bearing) {
        this.ID = ID;
        this.bearing = bearing;

        makeLights();
        makeSensors();
    }

    private void makeLights() {
        myLights = new ArrayList<>();

        GUILight light = new GUILight(0, LightShape.Circle, bearing);

        myLights.add(light);
    }
    private void makeSensors() {
        mySensors = new ArrayList<>();

        GUISensor sensor = new GUISensor(0);

        mySensors.add(sensor);
    }

    public LightCol getLightCol(int lightID){
        return myLights.get(lightID).getColor();
    }
    public void updateSensor(int ID, boolean isActive){
        mySensors.get(ID).setActive(isActive);
    }
    public void updateLights(int LightID, LightCol Color){
        GUILight theLight = myLights.get(LightID);
        theLight.changeColor(Color);
    }

    // Get this lane's ID
    public int getID() {

        return ID;
    }


    // Get this lane's direction
    public Bearing getBearing() {

        return bearing;
    }
}