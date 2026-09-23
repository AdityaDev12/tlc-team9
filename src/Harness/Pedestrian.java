package Harness;

import Communication.PedestrianSignalState;

public class Pedestrian {
    private PedestrianSignalState state;

    public Pedestrian() {
        state = PedestrianSignalState.WAIT;
    }

    public PedestrianSignalState getState() {
        return state;
    }

    public void walk() {
        state = PedestrianSignalState.WALK;
    }

    public void stop() {
        state = PedestrianSignalState.WAIT;
    }
}
