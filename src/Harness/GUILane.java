import java.util.ArrayList;

public class GUILane {
    private int ID;
    private ArrayList<GUILight> myLights;
    private Sensor mySensor;

    public GUILane(int ID) {
        this.ID = ID;

        makeLights();
    }

    public void makeLights() {
        myLights = new ArrayList<>();
    }
}