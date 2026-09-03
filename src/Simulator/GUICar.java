package Simulator;

public class GUICar {
    private GUILane myLane;
    private Bearing myBearing;
    private int ID;
    private boolean isAlive = true;
    private int laneID;
    private double distance;
    private GUIIntersection intersection;

    private boolean allowedToMove = true;
    private boolean committed = false; // once true, ignore future light changes - already entered

    private boolean sensorActive = false;
    private boolean isEMS;

    public GUICar(int ID, GUILane myLane, Bearing myBearing, int laneID, GUIIntersection intersection, boolean isEMS) {
        this.ID = ID;
        this.myLane = myLane;
        this.myBearing = myBearing;
        this.distance = 0;
        this.laneID = laneID;
        this.intersection = intersection;
        this.isEMS = isEMS;
    }

    public boolean canMove() { return allowedToMove; }
    public boolean isEMS() { return isEMS; }
    public void setCanMove(boolean allowedToMove) { this.allowedToMove = allowedToMove; }
    public Bearing getBearing() { return myBearing; }
    public double getDistance() { return distance; }
    public boolean isSensorActive() { return sensorActive; }

    public void addDistance(double amount) {
        distance += amount;
        updateMovement();
    }

    private void updateMovement() {
        if (distance >= 50 && !committed) {

            if (!sensorActive) {
                myLane.updateSensor(0, true);
                sensorActive = true;
            }

            if (myLane.getLightCol(0) == LightCol.Green) {
                allowedToMove = true;
                committed = true; // locked in - a later light change can't stop this car anymore
            } else {
                allowedToMove = false;
            }
        }
    }

    public void leaveSensor() {
        if (sensorActive) {
            myLane.updateSensor(0, false);
            sensorActive = false;
        }
    }
}