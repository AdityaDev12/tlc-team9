package Harness;

import Simulator.Bearing;

public class EMSVehicle {
    private boolean NSActive;
    private boolean EWActive;
    private final Mux mux;


    public EMSVehicle(Mux mux) {
        this.mux = mux;
        NSActive = false;
        EWActive = false;
    }

    public void incomingEMSVehicle(Bearing EMSBearing) {
        if(EMSBearing == Bearing.North ||  EMSBearing == Bearing.South) {
            NSActive = true;
        }
        if(EMSBearing == Bearing.East ||  EMSBearing == Bearing.West) {
            EWActive = true;
        }
    }

    public void leavingEMSVehicle(Bearing EMSBearing) {
        if(EMSBearing == Bearing.North ||   EMSBearing == Bearing.South) {
            NSActive = false;
        }
        if(EMSBearing == Bearing.East ||  EMSBearing == Bearing.West) {
            EWActive = false;
        }
    }
    public boolean isEMSActive() {
        return NSActive || EWActive;
    }
    public boolean isNSActive() {
        return NSActive;
    }
    public boolean isEWActive() {
        return EWActive;
    }
    public Bearing getActiveBearing() {
        if (NSActive) {
            return Bearing.North;
        }
        if (EWActive) {
            return Bearing.East;
        }
        return null;
    }
}
