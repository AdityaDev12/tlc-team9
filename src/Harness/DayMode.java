package Harness;

import java.util.Timer;

public class DayMode {
    private TrafficLights trafficLights;
    private Timer timer;

    public DayMode(
            TrafficLights trafficLights,
            Timer timer) {
        this.trafficLights = trafficLights;
        this.timer = timer;
    }

    public void run() throws InterruptedException {
        //NS Green Arrow
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

        if (isEMSReq() == true){

        }

    }
}
