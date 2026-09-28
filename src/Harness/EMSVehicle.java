package Harness;

import Simulator.Bearing;

public class EMSVehicle {
    private boolean NSActive;
    private boolean EWActive;

    public EMSVehicle() {
    }

    public void incomingEMSVehicle(Bearing EMSBearing) {
        if(EMSBearing.equals(Bearing.North) ||  EMSBearing.equals(Bearing.South)) {
            NSActive = true;
        }
        if(EMSBearing.equals(Bearing.East) ||  EMSBearing.equals(Bearing.West)) {
            EWActive = true;
        }
    }

    public void leavingEMSVehicle(Bearing EMSBearing) {
        if(EMSBearing.equals(Bearing.North) ||   EMSBearing.equals(Bearing.South)) {
            NSActive = false;
        }
        if(EMSBearing.equals(Bearing.East) ||  EMSBearing.equals(Bearing.West)) {
            EWActive = false;
        }
    }
    public boolean isEMSActive() {
        return NSActive && EWActive;
    }
    public boolean isNSActive() {
        return NSActive;
    }
    public boolean isEWActive() {
        return EWActive;
    }

}
