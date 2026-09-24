package Harness;

import Communication.PedestrianSignalState;

import java.util.Timer;

public class PedestrianMode {
    private Pedestrian pedestrian;
    private final long duration = 15_000;
    private TrafficLights trafficLights;
    private Timer timer;

    private long startTime; //time when walk started

    public PedestrianMode(
            Pedestrian pedestrian,
            TrafficLights trafficLights, Timer timer) {
        this.pedestrian = pedestrian;
        this.startTime = 0;
        this.trafficLights = trafficLights;
        this.timer = timer;
    }

    public void handlePedRequest() {
        if(pedestrian.getState() == PedestrianSignalState.WAIT) {
            //walk
            pedestrian.pedWalk();

            //set timer
            startTime = System.currentTimeMillis();
        }
    }

    //update timer
    public void update() {
        if(pedestrian.getState() == PedestrianSignalState.WALK) {
            long elasped = System.currentTimeMillis() - startTime;

            if(elasped >= duration) {
                timeout();
            }
        }
    }

    public void timeout() {
        if(pedestrian.getState() == PedestrianSignalState.WALK) {
            //stop
            pedestrian.pedStop();

            startTime = 0;
        }
    }

    public void reset() {
        pedestrian.pedStop();
        startTime = 0;
    }

    public PedestrianSignalState getState() {
        return pedestrian.getState();
    }
}
