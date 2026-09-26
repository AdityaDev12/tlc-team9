package Harness;

import Communication.PedestrianSignalState;

public class Pedestrian {
    private PedestrianSignalState state;
    private Mux mux;

    public Pedestrian(Mux mux) {
        this.mux = mux;
        state = PedestrianSignalState.WAIT;
    }
    public void pedRequest() {
        // notify a pedestrian requested to cross
    }

    public PedestrianSignalState getState() {
        return state;
    }

    public void pedWalk() {
        state = PedestrianSignalState.WALK;
    }

    public void pedStop() {
        state = PedestrianSignalState.WAIT;
    }
}
