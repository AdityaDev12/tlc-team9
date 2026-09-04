package Simulator;

public class GUICar {
    private GUILane myLane;
    private Bearing myBearing;
    private int ID;
    private boolean isAlive = true;
    private LanePosition lanePosition; //TODO will need to standardise laneNumber
    private double distance;
    private GUIIntersection intersection;

    private boolean allowedToMove = true;

    private boolean sensorActive = false;

    private boolean isEMS;


    public GUICar(int ID, GUILane myLane, Bearing myBearing, LanePosition lanePosition, GUIIntersection intersection, boolean isEMS) {
        this.ID = ID;
        this.myLane = myLane;
        this.myBearing = myBearing;
        this.distance = 0;
        this.lanePosition = lanePosition;
        this.intersection = intersection;
        this.isEMS = isEMS;
    }

    public boolean canMove() {
        return allowedToMove;
    }

    public boolean isEMS() {
        return isEMS;
    }

    public void setCanMove(boolean allowedToMove) {
        this.allowedToMove = allowedToMove;
    }

    public Bearing getBearing() {
        return myBearing;
    }

    public double getDistance() {
        return distance;
    }


    // Adds to the distance whenever the JavaFX car moves
    public void addDistance(double amount) {

        distance += amount;

        updateMovement();
    }

    private void updateMovement() {
        if (distance >= 50) {

            //activate the lane sensor
            if (!sensorActive) {

                myLane.updateSensor(0, true);

                sensorActive = true;
            }


            // Check the current traffic light
            if (myLane.getLightCol(0) == LightCol.Green) {

                //go
                allowedToMove = true;

            } else {

                //stop
                allowedToMove = false;
            }
        }

    }

    // Called when the car has passed the sensor/intersection area
    public void leaveSensor() {

        if (sensorActive) {

            myLane.updateSensor(0, false);

            sensorActive = false;
        }
    }
}