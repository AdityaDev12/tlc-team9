package Harness;

import Communication.PedestrianSignalState;
import javafx.animation.Timeline;

public class PedestrianMode {
    private Pedestrian pedestrian;
    private final long duration = 15_000;

    private long startTime; //time when walk started

    public PedestrianMode(Pedestrian pedestrian) {
        this.pedestrian = pedestrian;
        this.startTime = 0;
    }

    public void pedRequest() {
        if(pedestrian.getState() == PedestrianSignalState.WAIT) {
            //walk
            pedestrian.walk();

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
            pedestrian.stop();

            startTime = 0;
        }
    }

    public void reset() {
        pedestrian.stop();
        startTime = 0;
    }

    public PedestrianSignalState getState() {
        return pedestrian.getState();
    }
}
