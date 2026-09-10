package Simulator;

import java.util.ArrayList;

public class GUILane {
    private LanePosition lanePosition;
    private Bearing bearing;
    private ArrayList<GUILight> myLights = new ArrayList<>();
    private ArrayList<GUISensor> mySensors = new ArrayList<>();


    public GUILane(LanePosition lanePosition, Bearing bearing) {
        this.lanePosition = lanePosition;
        this.bearing = bearing;

        makeLights();
        makeSensors();
    }

    private void makeLights() {
        myLights = new ArrayList<>();

        GUILight light = new GUILight(0, LightShape.Square, bearing);

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

    //get lane position
    public LanePosition getLanePosition() {
        return lanePosition;
    }

    // Get this lane's direction
    public Bearing getBearing() {

        return bearing;
    }
}