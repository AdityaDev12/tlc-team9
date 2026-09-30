package Harness;

import Communication.PedestrianSignalState;

import java.util.TimerTask;

public class PedestrianMode {
    private final Pedestrian pedestrian;
    private final long duration = 15_000;
    private TrafficLights trafficLights;
    private final TLCTimer timer;

    public PedestrianMode(
            Pedestrian pedestrian,
            TrafficLights trafficLights, TLCTimer timer) {
        this.pedestrian = pedestrian;
        this.trafficLights = trafficLights;
        this.timer = timer;
    }

    public void handlePedRequest() throws InterruptedException{
        if(pedestrian.getState() == PedestrianSignalState.WAIT) {
            System.out.println("PedestrianMode: Starting ped crossing.");
            //walk
            pedestrian.pedWalk();
            System.out.println("PedestrianMode: Starting signal set to WALK.");
            timer.waitFor(duration);
            pedestrian.pedStop();
            System.out.println("PedestrianMode: Ped crossing finished.");
        }
    }

    public void timeout() {
        if(pedestrian.getState() == PedestrianSignalState.WALK) {
            System.out.println("PedestrianMode: Ped crossing finished.");
            //stop
            pedestrian.pedStop();
        }
    }

    public void reset() {
        pedestrian.pedStop();
    }

    public PedestrianSignalState getState() {
        return pedestrian.getState();
    }
}
