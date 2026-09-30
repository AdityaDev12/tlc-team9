package Harness;

import Simulator.Bearing;

public class EMSMode {
    private final Antenna antenna;
    private final TrafficLights trafficLights;
    private final TLCTimer timer;
    // direction of active emsRequest
    private Bearing activeBearing;

    public EMSMode(
            Antenna antenna,
            TrafficLights trafficLights,
            TLCTimer timer) {
        this.antenna = antenna;
        this.trafficLights = trafficLights;
        this.timer = timer;
        this.activeBearing = null;
    }
    public void run() throws InterruptedException {
        if (activeBearing == null) {
            return;
        }
        // NS emsRequest
        if (activeBearing == Bearing.North || activeBearing == Bearing.South) {
            trafficLights.setLightPattern(LightPattern.NS_GREEN);
        }
        // EW emsRequest
        else if (activeBearing == Bearing.East || activeBearing == Bearing.West) {
            trafficLights.setLightPattern(LightPattern.EW_GREEN);
        }
        // stay until Main/ModeControl clears request
    }
    public void request(Bearing bearing) {
        activeBearing = bearing;
    }
    public void clear(Bearing bearing) {
        if (activeBearing == bearing) {
            activeBearing = null;
        }
    }
    public Bearing getActiveBearing() {
        return activeBearing;
    }
}
