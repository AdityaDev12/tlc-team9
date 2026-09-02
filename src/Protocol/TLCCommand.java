package Protocol;

/**
 * Every valid command type in the TLC instruction protocol.
 *
 * LIGHT = carried by InstructionMessage (server -> GUI light-state renders).
 * EVENT = carried by SimulatorEvent, in either direction (all other traffic).
 */
public enum TLCCommand {

    // ---- LIGHT: carried by InstructionMessage ----
    SET_LIGHT_STATE,          // target = "NORTH_THROUGH" etc, value = RED|YELLOW|GREEN

    // ---- EVENT: carried by SimulatorEvent (either direction) ----
    UPDATE_PEDESTRIAN_SIGNAL, // target = "NORTH" (crossing id), value = WALK|WAIT
    TRIGGER_FAIL_SAFE,        // target = "ALL", value = FLASH_RED|FLASH_YELLOW
    RESUME_NORMAL,            // target = "ALL", value = "NORMAL"
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
