package Harness;

import Simulator.Bearing;

public class Antenna {
    private Mux mux;
    public Antenna(Mux mux) {
        this.mux = mux;
    }
    public void emsRequest(Bearing bearing) {
        // process ems request and notify other interfaces
    }
    public void emsCleared(Bearing bearing) {
        // process ems clearing intersection
    }
    public void resetRequest() {
        // clear currently active request
    }
}
