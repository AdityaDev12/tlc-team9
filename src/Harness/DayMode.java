package Harness;

public class DayMode {
    private TrafficLights trafficLights;
    private TLCTimer timer;
    private EMSVehicle emsVehicle;


    public DayMode(
            TrafficLights trafficLights,
            TLCTimer timer, EMSVehicle emsVehicle) {
        this.trafficLights = trafficLights;
        this.timer = timer;
        this.emsVehicle = emsVehicle;
    }

    public void run() throws InterruptedException {
        //NS Green
        trafficLights.setLightPattern(LightPattern.NS_ARROW_GREEN);
        timer.waitFor(15000);

        trafficLights.setLightPattern(LightPattern.NS_ARROW_YELLOW);
        timer.waitFor(5000);

        trafficLights.setLightPattern(LightPattern.NS_GREEN);
        timer.waitFor(20000);

        trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        timer.waitFor(5000);

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.waitFor(2000);

        if (emsVehicle.isEMSActive()){
            //RETURN TO POWER ON
        }

        //EW Green
        trafficLights.setLightPattern(LightPattern.EW_ARROW_GREEN);
        timer.waitFor(15000);

        trafficLights.setLightPattern(LightPattern.EW_ARROW_YELLOW);
        timer.waitFor(5000);

        trafficLights.setLightPattern(LightPattern.EW_GREEN);
        timer.waitFor(20000);

        trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        timer.waitFor(5000);

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.waitFor(2000);

        //Reset method (if necessary)
    }
}
