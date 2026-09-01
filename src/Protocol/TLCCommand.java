package Protocol;

/**
 * Every valid command type in the TLC instruction protocol.
 *
 * INBOUND  = sent by the controller/server, consumed by your GUI layer
 *            (you parse these and call into the JavaFX animation controller).
 * OUTBOUND = sent by your GUI layer out to the server
 *            (simulated hardware events: sensors, buttons, EMS receiver).
 *
 * Keeping both directions in one enum keeps the protocol single-source-of-truth:
 * everyone on the team imports this file instead of hardcoding strings.
 */
public enum TLCCommand {

    // ---- INBOUND: server -> GUI (render instructions) ----
    SET_LIGHT_STATE,          // target = "NORTH_THROUGH" etc, value = RED|YELLOW|GREEN
    UPDATE_PEDESTRIAN_SIGNAL, // target = "NORTH" (crossing id), value = WALK|WAIT
    TRIGGER_FAIL_SAFE,        // target = "ALL", value = FLASH_RED|FLASH_YELLOW
    RESUME_NORMAL,            // target = "ALL", value = "NORMAL"

    // ---- OUTBOUND: GUI -> server (simulated hardware events) ----
    VEHICLE_DETECTED,           // target = lane id, value = "DETECTED"
    VEHICLE_CLEARED,            // target = lane id, value = "CLEARED"
    PEDESTRIAN_BUTTON_PRESSED,  // target = crossing id, value = "PRESSED"
    EMS_PRIORITY_REQUEST,       // target = direction, value = "REQUEST"
    EMS_PRIORITY_CANCEL;        // target = direction, value = "CANCEL"

    /** Converts a raw wire token back into an enum value, with a clear error on typos. */
    public static TLCCommand fromWire(String token) {
        try {
            return TLCCommand.valueOf(token.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown TLC command token: '" + token + "'", e);
        }
    }
}
