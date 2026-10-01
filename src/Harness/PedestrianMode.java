package Harness;

import Communication.PedestrianSignalState;

public class PedestrianMode {
    private final Pedestrian pedestrian;
    private final long duration = 15_000;
    private TrafficLights trafficLights;
    private final TLCTimer timer;
    private volatile boolean emsRequested;

    public PedestrianMode(
            Pedestrian pedestrian,
            TrafficLights trafficLights, TLCTimer timer) {
        this.pedestrian = pedestrian;
        this.trafficLights = trafficLights;
        this.timer = timer;
        this.emsRequested = false;
    }

    public void setEmsRequested(boolean requested) {
        emsRequested = requested;
    }

    public boolean run() throws InterruptedException{
        if(pedestrian.getState() != PedestrianSignalState.WAIT) {
            return true;
        }

        // check for ems first
        if (emsRequested) {
            System.out.println("PedestrianMode: EMS already waiting. " + "Cancelling pedestrian request.");
            return false;
        }

        LightPattern current = trafficLights.getCurrentPattern();

        //turn whichever direction is currently active to yellow
        if(current == LightPattern.NS_GREEN || current == LightPattern.NS_ARROW_GREEN) {
            trafficLights.setLightPattern(LightPattern.NS_YELLOW);
        } else if (current == LightPattern.EW_GREEN || current == LightPattern.EW_ARROW_GREEN) {
            trafficLights.setLightPattern(LightPattern.EW_YELLOW);
        }
        //time for yellow
        if (!timer.waitFor(5000, () -> emsRequested)) {
            System.out.println("PedestrianMode: EMS request arrived during yellow.");
            trafficLights.setLightPattern(LightPattern.ALL_RED);
            return false;
        }

        trafficLights.setLightPattern(LightPattern.ALL_RED);

        // if EMS arrived during transition, don't start crossing
        if (!timer.waitFor(2000, () -> emsRequested)) {
            System.out.println("PedestrianMode: EMS request arrive. " + "Cancelling pedestrian crossing before WALK.");
            return false;
        }

        // check again before WALK
        if (emsRequested) {
            System.out.println("PedestrianMode: EMS waiting. " + "Cancelling pedestrian crossing before WALK.");
            return false;
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

