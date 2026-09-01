package Protocol;

import java.time.Instant;

/**
 * A single parsed instruction, in both directions of the protocol.
 *
 * Wire format (pipe-delimited, 4 fields):
 *   COMMAND:TARGET:VALUE:TIMESTAMP
 *
 * Example lines:
 *   SET_LIGHT_STATE:NORTH_THROUGH:GREEN:1735599999000
 *   PEDESTRIAN_BUTTON_PRESSED:NORTH:PRESSED:1735599999500
 *
 * Why a delimited string instead of JSON: your values (enum names, lane ids)
 * never contain the delimiter, so this avoids pulling in a JSON library just
 * for four flat fields, and it's trivial to log/debug by eye. If the team
 * later needs nested data, swap toWireFormat()/parse() for a JSON library —
 * every other class only depends on this class, not on the wire format itself.
 */
public final class InstructionMessage {

    private static final String DELIMITER = ":";

    private final TLCCommand command;
    private final String target;
    private final String value;
    private final long timestampMillis;

    public InstructionMessage(TLCCommand command, String target, String value, long timestampMillis) {
        this.command = command;
        this.target = target;
        this.value = value;
        this.timestampMillis = timestampMillis;
    }

    /** Convenience factory that stamps the current time — use this when sending. */
    public static InstructionMessage of(TLCCommand command, String target, String value) {
        return new InstructionMessage(command, target, value, Instant.now().toEpochMilli());
    }

    public TLCCommand getCommand() { return command; }
    public String getTarget() { return target; }
    public String getValue() { return value; }
    public long getTimestampMillis() { return timestampMillis; }

    /** Serializes this message to the wire format string sent over the socket. */
    public String toWireFormat() {
        return String.join(DELIMITER, command.name(), target, value, String.valueOf(timestampMillis));
    }

    /** Parses one raw line read off the socket back into a message object. */
    public static InstructionMessage parse(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            throw new IllegalArgumentException("Cannot parse empty instruction line");
        }
        // limit=4 so a value that legitimately contains no extra colons is fine;
        // if a value ever needs a colon, this still parses because TARGET/VALUE never do.
        String[] parts = rawLine.trim().split(DELIMITER, 4);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Malformed instruction, expected 4 fields: " + rawLine);
        }
        TLCCommand command = TLCCommand.fromWire(parts[0]);
        String target = parts[1];
        String value = parts[2];
        long timestamp;
        try {
            timestamp = Long.parseLong(parts[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid timestamp field in: " + rawLine, e);
        }
        return new InstructionMessage(command, target, value, timestamp);
    }

    @Override
    public String toString() {
        return toWireFormat();
    }
}
