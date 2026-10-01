package Harness;

import Simulator.Bearing;

public class EMSVehicle {
    private final Mux mux;
    private Bearing activeBearing;


    public EMSVehicle(Mux mux) {
        this.mux = mux;
        this.activeBearing = null;
    }

    public void incomingEMSVehicle(Bearing bearing) {
        activeBearing = bearing;
    }

    public void leavingEMSVehicle(Bearing bearing) {
        if(activeBearing == bearing) {
            activeBearing = null;
        }
    }
    public boolean isEMSActive() {
        return activeBearing != null;
    }
    public boolean isNSActive() {
        return activeBearing == Bearing.North || activeBearing == Bearing.South;
    }
    public boolean isEWActive() {
        return activeBearing == Bearing.East || activeBearing == Bearing.West;
    }
    public Bearing getActiveBearing() {
        return activeBearing;
    }
}
