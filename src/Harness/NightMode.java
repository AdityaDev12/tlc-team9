package Harness;

public class NightMode {
    private final TrafficLights trafficLights;
    private final TrafficSensor trafficSensorEW;
    private final TLCTimer timer;
    private volatile boolean emsRequested;
    private final EMSVehicle emsVehicle;

    public NightMode(
            TrafficSensor trafficSensorEW,
            TrafficLights trafficLights,
            TLCTimer timer, EMSVehicle emsVehicle) {
        this.trafficSensorEW = trafficSensorEW;
        this.trafficLights = trafficLights;
        this.timer = timer;
        this.emsRequested = false;
        this.emsVehicle = emsVehicle;
    }

    public void setEmsRequested(boolean requested) {
        emsRequested = requested;
    }

    public void setPedestrianRed() throws InterruptedException {

        LightPattern current = trafficLights.getCurrentPattern();

        if(current == LightPattern.NS_GREEN || current == LightPattern.NS_ARROW_GREEN) {
            trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        } else if (current == LightPattern.EW_GREEN || current == LightPattern.EW_ARROW_GREEN) {
            trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        }

        timer.waitFor(5000);
        trafficLights.setLightPattern(LightPattern.ALL_RED);
    }

    public boolean run() throws InterruptedException {
        // NS has priority
        trafficLights.setLightPattern(LightPattern.NS_GREEN);
        // minimum green always given to NS
        if (!timer.waitFor(20000, () -> emsRequested)) {
            return false;
        }

        // check if EW is waiting
        if (trafficSensorEW.isActive()) {
            // NS Yellow
            trafficLights.setLightPattern(LightPattern.NS_YELLOW);
            if (!timer.waitFor(5000, () -> emsRequested)) {
                return false;
            }
            // All Red
            trafficLights.setLightPattern(LightPattern.ALL_RED);
            if (!timer.waitFor(2000, () -> emsRequested)) {
                return false;
            }

            // EW Green traffic turn
            trafficLights.setLightPattern(LightPattern.EW_GREEN);
            if (!timer.waitFor(10000, () -> emsRequested)) {
                return false;
            }

            // EW Yellow
            trafficLights.setLightPattern(LightPattern.EW_YELLOW);
            if (!timer.waitFor(5000, () -> emsRequested)) {
                return false;
            }
            // All Red
            trafficLights.setLightPattern(LightPattern.ALL_RED);
            if (!timer.waitFor(2000, () -> emsRequested)) {
                return false;
            }
        }
        return true;
    }
}
