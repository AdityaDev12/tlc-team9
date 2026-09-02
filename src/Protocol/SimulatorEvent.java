package Protocol;

/**
 * A single event message, carried in either direction (server <-> GUI) for
 * every {@link TLCCommand} other than SET_LIGHT_STATE. Plain data holder;
 * wire-format building/parsing lives in {@link Protocol}.
 */
public final class SimulatorEvent {

    private final TLCCommand command;
    private final String target;
    private final String value;

    public SimulatorEvent(TLCCommand command, String target, String value) {
        this.command = command;
        this.target = target;
        this.value = value;
    }

    public TLCCommand getCommand() { return command; }
    public String getTarget() { return target; }
    public String getValue() { return value; }

    @Override
    public String toString() {
        return Protocol.buildEvent(command, target, value);
    }
}
