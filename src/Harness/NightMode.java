package Harness;

import java.util.Timer;

public class NightMode {
    private TrafficLights trafficLights;
    private TrafficSensor trafficSensor;
    private Timer timer;

    public NightMode(
            TrafficSensor trafficSensor,
            TrafficLights trafficLights,
            Timer timer) {
        this.trafficLights = trafficLights;
        this.trafficSensor = trafficSensor;
        this.timer = timer;
    }

    public void run() {
        // implment nightm ode logic here
    }
}
