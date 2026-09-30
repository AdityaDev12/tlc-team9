package Harness;

import Simulator.Bearing;

public class TrafficSensor {
    private final Mux mux;
    private final Bearing bearing;
    private boolean isActive;

    public TrafficSensor(Mux mux, Bearing bearing) {
            this.mux = mux;
            this.bearing = bearing;
            this.isActive = false;
        }

    public void vehicleDetected() {
        isActive = true;
    }
    public void vehicleGone(){
        isActive = false;
    }
    public boolean isActive() {
        return isActive;
    }
    public Bearing getBearing() {
        return bearing;
    }

}
