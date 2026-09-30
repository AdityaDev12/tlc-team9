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
        //NS Green

        trafficLights.setLightPattern(LightPattern.NS_GREEN);

        while(!trafficSensorEW.isActive()) {
            timer.waitFor(100);
        }

        trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        timer.waitFor(5000);

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.waitFor(2000);

        if (emsVehicle.isEMSActive()){
            //RETURN TO POWER ON
        }

        //EW Green
        trafficLights.setLightPattern(LightPattern.EW_GREEN);
        timer.waitFor(20000);

        trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        timer.waitFor(5000);

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.waitFor(2000);

        //Reset method (if necessary)
    }
}
