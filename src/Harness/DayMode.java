package Harness;

import java.util.Timer;

public class DayMode {
    private TrafficSensor trafficSensor;
    private TrafficLights trafficLights;
    private Timer timer;

    public DayMode(
            TrafficSensor trafficSensor,
            TrafficLights trafficLights,
            Timer timer) {
        this.trafficSensor = trafficSensor;
        this.trafficLights = trafficLights;
        this.timer = timer;
    }

    public void run() {
        // implement day mode control logic here
    }
}
