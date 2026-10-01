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
}
