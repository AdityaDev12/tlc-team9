package Harness;

import Simulator.Bearing;

public class TrafficSensor {
    private Mux mux;
    public TrafficSensor(Mux mux) {
        this.mux = mux;
    }
    public void vehicleDetected(Bearing bearing) {
        // process detected vehicle for daymode/nightmode
    }
}
