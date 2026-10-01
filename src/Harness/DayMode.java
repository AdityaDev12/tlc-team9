package Harness;

public class DayMode {
    private TrafficLights trafficLights;
    private TLCTimer timer;
    private volatile boolean emsRequested;
    private EMSVehicle emsVehicle;


    public DayMode(
            TrafficLights trafficLights,
            TLCTimer timer, EMSVehicle emsVehicle) {
        this.trafficLights = trafficLights;
        this.timer = timer;
        this.emsVehicle = emsVehicle;
        this.emsRequested = false;
    }
    public void setEmsRequested(boolean requested) {
        emsRequested = requested;
    }

    public boolean runNS() throws InterruptedException {
        //NS Green
        trafficLights.setLightPattern(LightPattern.NS_ARROW_GREEN);
        if (!timer.waitFor(15000, () -> emsRequested)) {
            return false;
        }

        trafficLights.setLightPattern(LightPattern.NS_ARROW_YELLOW);
        if (!timer.waitFor(5000, () -> emsRequested)) {
            return false;
        }

        trafficLights.setLightPattern(LightPattern.NS_GREEN);
        if (!timer.waitFor(20000, () -> emsRequested)) {
            return false;
        }

        trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        if (!timer.waitFor(5000, () -> emsRequested)) {
            return false;
        }

        System.out.println("NS: ALL RED");

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        if (!timer.waitFor(2000, () -> emsRequested)) {
            return false;
        }
        System.out.println("NS: ALL RED finished");
        return true;

    }

    public boolean runEW() throws InterruptedException {

        //EW Green
        trafficLights.setLightPattern(LightPattern.EW_ARROW_GREEN);
        if (!timer.waitFor(15000, () -> emsRequested)) {
            return false;
        }

        trafficLights.setLightPattern(LightPattern.EW_ARROW_YELLOW);
        if (!timer.waitFor(5000, () -> emsRequested)) {
            return false;
        }

        trafficLights.setLightPattern(LightPattern.EW_GREEN);
        if (!timer.waitFor(20000, () -> emsRequested)) {
            return false;
        }

        trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        if (!timer.waitFor(5000, () -> emsRequested)) {
            return false;
        }

        System.out.println("EW: ALL RED");

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        if (!timer.waitFor(2000, () -> emsRequested)) {
            return false;
        }

        System.out.println("EW: ALL RED finished");
        return true;
    }
}
