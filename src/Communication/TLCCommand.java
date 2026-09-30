package Communication;

/**
 * Every valid command type in the TLC instruction protocol.
 * INBOUND  = sent by the controller/server, consumed by your GUI layer
 *            (you parse these and call into the JavaFX animation controller).
 * OUTBOUND = sent by your GUI layer out to the server
 *            (simulated hardware events: sensors, buttons, EMS receiver).
 * Keeping both directions in one enum keeps the protocol single source of truth:
 */
public enum TLCCommand {


    SET_LIGHT_STATE,
    SET_PEDESTRIAN_SIGNAL,
    UPDATE_PEDESTRIAN_SIGNAL,
    TRIGGER_FAIL_SAFE,
    RESUME_NORMAL,


    VEHICLE_DETECTED,
    VEHICLE_CLEARED,
    PEDESTRIAN_BUTTON_PRESSED,
    EMS_PRIORITY_REQUEST,
    EMS_PRIORITY_CANCEL;

    /** Converts a raw wire token back into an enum value, with a clear error on typos. */
    public static TLCCommand fromWire(String token) {
        try {
            return TLCCommand.valueOf(token.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown TLC command token: '" + token + "'", e);
        }
    }
}
