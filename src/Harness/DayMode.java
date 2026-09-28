package Harness;

import Simulator.Bearing;

import java.util.Timer;

public class DayMode {
    private TrafficLights trafficLights;
    private Timer timer;
    private EMSVehicle emsVehicle;


    public DayMode(
            TrafficLights trafficLights,
            Timer timer, EMSVehicle emsVehicle) {
        this.trafficLights = trafficLights;
        this.timer = timer;
        this.emsVehicle = new EMSVehicle();
    }

    public void run() throws InterruptedException {
        //NS Green
        trafficLights.setLightPattern(LightPattern.NS_ARROW_GREEN);
        timer.wait(15000);

        trafficLights.setLightPattern(LightPattern.NS_ARROW_YELLOW);
        timer.wait(5000);

        trafficLights.setLightPattern(LightPattern.NS_GREEN);
        timer.wait(20000);

        trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        timer.wait(5000);

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.wait(2000);

        if (emsVehicle.isEMSActive()){
            //RETURN TO POWER ON
        }

        //EW Green
        trafficLights.setLightPattern(LightPattern.EW_ARROW_GREEN);
        timer.wait(15000);

        trafficLights.setLightPattern(LightPattern.EW_ARROW_YELLOW);
        timer.wait(5000);

        trafficLights.setLightPattern(LightPattern.EW_GREEN);
        timer.wait(20000);

        trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        timer.wait(5000);

        trafficLights.setLightPattern(LightPattern.ALL_RED);
        timer.wait(2000);

        //Reset method (if necessary)
    }
}
