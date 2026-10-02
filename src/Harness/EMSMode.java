package Harness;

import Simulator.Bearing;

import java.util.LinkedList;
import java.util.Queue;

/**
 * EMS Mode (SRS 5.5 EMS Signal)
 *
 * PowerOn --emsRequest(dir)--> EMS Request : green for the EMS axis
 * EMS Request --emsCleared(dir)--> EMS Passed : resetRequest, yellow for 5 s
 * EMS Passed --timeout--> PowerOn : all red for 2 s
 */
public class EMSMode {
    private final Antenna antenna;
    private final TrafficLights trafficLights;
    private final TLCTimer timer;

    // direction of the EMS request currently being served
    private volatile Bearing activeBearing;

    private static final long YELLOW_TIME = 5000;   // set(5)
    private static final long ALL_RED_TIME = 2000;  // set(2)
    private static final long CHECK_TIME = 100;     // how often we check if EMS passed
    private static final long MIN_GREEN = 10000;    // EMS always gets at least 10 s of green

    public EMSMode(Antenna antenna, TrafficLights trafficLights, TLCTimer timer) {
        this.antenna = antenna;
        this.trafficLights = trafficLights;
        this.timer = timer;
        this.activeBearing = null;
    }

    // called by ModeControl when an EMS request arrives
    public synchronized void request(Bearing bearing) {
        activeBearing = bearing;
    }

    // runs the EMS state machine once, returns when the EMS vehicle has passed
    public void run() throws InterruptedException {


        Bearing bearing;

        synchronized (this) {
            bearing = activeBearing;
        }

        if(bearing == null) {
            return;
        }

        boolean isNS = isNorthSouth(bearing);
        //String axis = isNS ? "NS" : "EW";
        System.out.println("EMSMode: EMS request from " + bearing);

        // stop all traffic first so there are never two greens at once
        LightPattern current = trafficLights.getCurrentPattern();

        if(current == LightPattern.NS_GREEN || current == LightPattern.NS_ARROW_GREEN) {
            trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        } else if (current == LightPattern.EW_GREEN || current == LightPattern.EW_ARROW_GREEN) {
            trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        }

        timer.waitFor(YELLOW_TIME);
        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.waitFor(ALL_RED_TIME);

        // state: EMS Request -> green for the EMS direction
        if (isNS) {
            trafficLights.setLightPattern(LightPattern.NS_GREEN);
        } else {
            trafficLights.setLightPattern(LightPattern.EW_GREEN);
        }
        System.out.println("EMSMode: " + (isNS ? "NS" : "EW") + " green for EMS, waiting for it to pass");

        // hold green for at least MIN_GREEN, and longer while the antenna
        // still has a request on this axis
        long waited = 0;
        while (waited < MIN_GREEN || antenna.getActiveBearing() == bearing) {
            timer.waitFor(CHECK_TIME);
            waited = waited + CHECK_TIME;
        }
        System.out.println("EMSMode: EMS vehicle has passed.");

        // state: EMS Passed -> resetRequest, yellow
        if (isNS) {
            trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        } else {
            trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        }
        timer.waitFor(YELLOW_TIME);

        // timeout -> all red, back to PowerOn (ModeControl)
        trafficLights.setLightPattern(LightPattern.ALL_RED);

        synchronized (this) {
            activeBearing = null;
        }


        System.out.println("EMSMode: finished, back to normal");

    }

    // true if the antenna still has an EMS request on the NS axis (isNS) or EW axis
    private boolean antennaOnAxis(boolean isNS) {
        Bearing bearing = antenna.getActiveBearing();
        return bearing != null && isNorthSouth(bearing) == isNS;
    }

    private boolean isNorthSouth(Bearing bearing) {
        return bearing == Bearing.North || bearing == Bearing.South;
    }
}