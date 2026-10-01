package Harness;

import Communication.PedestrianSignalState;

public class PedestrianMode {
    private final Pedestrian pedestrian;
    private final long duration = 15_000;
    private final TLCTimer timer;
    public PedestrianMode(
            Pedestrian pedestrian, TLCTimer timer) {
        this.pedestrian = pedestrian;
        this.timer = timer;
    }


    public boolean run() throws InterruptedException{
        if(pedestrian.getState() != PedestrianSignalState.WAIT) {
            return true;
        }

        System.out.println("PedestrianMode: Starting ped crossing.");
        //walk
        pedestrian.pedWalk();

        System.out.println("PedestrianMode: Starting signal set to WALK.");
        timer.waitFor(duration);
        pedestrian.pedStop();
        System.out.println("PedestrianMode: Ped crossing finished.");

        //don't turn green right away
        timer.waitFor(2000);
        return true;
    }
}

