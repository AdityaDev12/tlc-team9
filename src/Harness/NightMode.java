package Harness;

public class NightMode {
    private TrafficLights trafficLights;
    private TrafficSensor trafficSensorEW;
    private TLCTimer timer;
    private EMSVehicle emsVehicle;

    public NightMode(
            TrafficSensor trafficSensorEW,
            TrafficLights trafficLights,
            TLCTimer timer, EMSVehicle emsVehicle) {
        this.trafficLights = trafficLights;
        this.trafficSensorEW = trafficSensorEW;
        this.timer = timer;
        this.emsVehicle = emsVehicle;
    }

    public void run() throws InterruptedException {
        // NS has priority
        trafficLights.setLightPattern(LightPattern.NS_GREEN);

        timer.waitFor(20000); // minimum green always given to NS

        // check if EW is waiting
        if (trafficSensorEW.isActive()) {
            // NS Yellow
            trafficLights.setLightPattern(LightPattern.NS_YELLOW);
            timer.waitFor(5000);
            // All Red
            trafficLights.setLightPattern(LightPattern.ALL_RED);
            timer.waitFor(2000);

            // EW Green traffic turn
            trafficLights.setLightPattern(LightPattern.EW_ARROW_GREEN);
            timer.waitFor(5000);

            trafficLights.setLightPattern(LightPattern.EW_ARROW_YELLOW);
            timer.waitFor(5000);

            trafficLights.setLightPattern(LightPattern.EW_GREEN);
            timer.waitFor(10000);
            // EW Yellow
            trafficLights.setLightPattern(LightPattern.EW_YELLOW);
            timer.waitFor(5000);
            // All Red
            trafficLights.setLightPattern(LightPattern.ALL_RED);
            timer.waitFor(2000);

            // Protected left turns for NS
            trafficLights.setLightPattern(LightPattern.NS_ARROW_GREEN);
            timer.waitFor(10000);

            trafficLights.setLightPattern(LightPattern.NS_ARROW_YELLOW);
            timer.waitFor(5000);
        }
    }
}
