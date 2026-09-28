package Harness;

import java.util.Timer;

public class NightMode {
    private TrafficLights trafficLights;
    private TrafficSensor trafficSensorEW;
    private Timer timer;
    private EMSVehicle emsVehicle;

    public NightMode(
            TrafficSensor trafficSensorEW,
            TrafficLights trafficLights,
            Timer timer, EMSVehicle emsVehicle) {
        this.trafficLights = trafficLights;
        this.trafficSensorEW = trafficSensorEW;
        this.timer = timer;
        this.emsVehicle = emsVehicle;
    }

    public void run() throws InterruptedException {
        //NS Green

        trafficLights.setLightPattern(LightPattern.NS_GREEN);

        //Wait until an EW sensor is activated
        boolean base = true;
        while (base) {
            if (trafficSensorEW.isActive()) {
                base = false;
            }
            timer.wait(100);
        }
        base = false;

        trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        timer.wait(5000);

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.wait(2000);

        if (emsVehicle.isEMSActive()){
            //RETURN TO POWER ON
        }

        //EW Green
        trafficLights.setLightPattern(LightPattern.EW_GREEN);
        timer.wait(20000);

        trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        timer.wait(5000);

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.wait(2000);

        //Reset method (if necessary)
    }
}
