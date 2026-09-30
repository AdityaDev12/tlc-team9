package Harness;

import Simulator.Bearing;

public class Antenna {
    private final Mux mux;
    private Bearing activeBearing;

    public Antenna(Mux mux) {
        this.mux = mux;
        this.activeBearing = null;
    }
    public void emsRequest(Bearing bearing) {
        activeBearing = bearing;
    }
    public void emsCleared(Bearing bearing) {
        if (activeBearing == bearing) {
            activeBearing = null;
        }
    }
    public void resetRequest() {
        activeBearing = null;
    }
    public Bearing getActiveBearing() {
        return activeBearing;
    }
}
