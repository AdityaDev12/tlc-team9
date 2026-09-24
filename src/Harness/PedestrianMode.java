package Harness;

import Communication.PedestrianSignalState;

import java.util.Timer;
import java.util.TimerTask;

public class PedestrianMode {
    private final Pedestrian pedestrian;
    private final long duration = 15_000;
    private TrafficLights trafficLights;
    private final Timer timer;

    public PedestrianMode(
            Pedestrian pedestrian,
            TrafficLights trafficLights, Timer timer) {
        this.pedestrian = pedestrian;
        this.trafficLights = trafficLights;
        this.timer = timer;
    }

    public void handlePedRequest() {
        if(pedestrian.getState() == PedestrianSignalState.WAIT) {
            //walk
            pedestrian.pedWalk();

            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    timeout();
                }
            }, duration);
        }
    }

    public void timeout() {
        if(pedestrian.getState() == PedestrianSignalState.WALK) {
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
