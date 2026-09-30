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

    public void runNS() throws InterruptedException {
        //NS Green
        trafficLights.setLightPattern(LightPattern.NS_ARROW_GREEN);
        timer.waitFor(15000);

        trafficLights.setLightPattern(LightPattern.NS_ARROW_YELLOW);
        timer.waitFor(5000);

        trafficLights.setLightPattern(LightPattern.NS_GREEN);
        timer.waitFor(20000);

        trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        timer.waitFor(5000);

        System.out.println("NS: ALL RED");

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.waitFor(2000);
        System.out.println("NS: ALL RED finished");

        if (emsVehicle.isEMSActive()) {
            //RETURN TO POWER ON
        }
    }

    public void runEW() throws InterruptedException {

        //EW Green
        trafficLights.setLightPattern(LightPattern.EW_ARROW_GREEN);
        timer.waitFor(15000);

        trafficLights.setLightPattern(LightPattern.EW_ARROW_YELLOW);
        timer.waitFor(5000);

        trafficLights.setLightPattern(LightPattern.EW_GREEN);
        timer.waitFor(20000);

        trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        timer.waitFor(5000);

        System.out.println("EW: ALL RED");

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.waitFor(2000);

        System.out.println("EW: ALL RED finished");

        //Reset method (if necessary)
    }
}
