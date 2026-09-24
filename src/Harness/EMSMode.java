package Harness;

import Simulator.Bearing;

import java.util.Timer;

public class EMSMode {
    private Antenna antenna;
    private TrafficLights trafficLights;
    private Timer timer;
    // direction of active emsRequest
    private Bearing activeBearing;

    public EMSMode(
            Antenna antenna,
            TrafficLights trafficLights,
            Timer timer) {
        this.antenna = antenna;
        this.trafficLights = trafficLights;
        this.timer = timer;

        activeBearing = null;
    }
    public void run() {
        // implement ems logic here
    }
    public void request(Bearing bearing) {
        activeBearing = bearing;
        // needs to give requested direction priority
    }
    public void clear(Bearing bearing) {
        // check if active ems bearing, transition through yellow and red, return to normal operation
    }
    public Bearing getActiveBearing() {
        return activeBearing;
    }
}
