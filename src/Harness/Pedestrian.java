package Harness;

import Communication.InstructionMessage;
import Communication.PedestrianSignalState;
import Communication.TLCCommand;

public class Pedestrian {
    private PedestrianSignalState state;
    private final Mux mux;

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
        InstructionMessage message = new InstructionMessage(TLCCommand.SET_PEDESTRIAN_SIGNAL, "ALL", PedestrianSignalState.WALK.name());
        mux.sendInstruction(message);
    }

    public void pedStop() {
        state = PedestrianSignalState.WAIT;
        InstructionMessage message = new InstructionMessage(TLCCommand.SET_PEDESTRIAN_SIGNAL,"ALL", PedestrianSignalState.WAIT.name());
        mux.sendInstruction(message);
    }
}
