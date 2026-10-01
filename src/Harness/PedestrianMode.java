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
            LightPattern current = trafficLights.getCurrentPattern();

            //turn whichever direction is currently active to yellow
            if(current == LightPattern.NS_GREEN || current == LightPattern.NS_ARROW_GREEN) {
                trafficLights.setLightPattern(LightPattern.NS_YELLOW);
            }

            else if (current == LightPattern.EW_GREEN || current == LightPattern.EW_ARROW_GREEN) {
                trafficLights.setLightPattern(LightPattern.EW_YELLOW);
            }

            //time for yellow
            timer.waitFor(5000);

            //all red
            trafficLights.setLightPattern(LightPattern.ALL_RED);

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
