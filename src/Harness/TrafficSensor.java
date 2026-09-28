package Harness;

import Simulator.Bearing;

public class TrafficSensor {
    private Mux mux;
    private Bearing bearing;
    private boolean isActive;
    public TrafficSensor(Mux mux, Bearing bearing) {
            this.mux = mux;
            this.bearing = bearing;
            this.isActive = false;
        }

    public void vehicleDetected() {
        this.isActive = true;
    }
    public void vehicleGone(){
        this.isActive = false;
    }
    public boolean isActive() {
        return this.isActive;
    }

}
