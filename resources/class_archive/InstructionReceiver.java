package Communication;

/**
 * Inbound half of your responsibility: raw socket line -> parsed message ->
 * routed to whichever part of the GUI/animation code cares.
 *
 * This is the Observer pattern: other classes (typically the JavaFX
 * animation controller owned by teammate #3) subscribe() a handler for each
 * TLCCommand they care about, and this class notifies all subscribers when
 * a matching message arrives. Neither side needs to know about the socket,
 * and the animation code never touches raw strings.
 */
/*
public class InstructionReceiver {

    private final Map<TLCCommand, List<CommandHandler>> handlers = new EnumMap<>(TLCCommand.class);

    public InstructionReceiver(NetworkChannel channel) {
        channel.setLineListener(this::onRawLine);
    }

    // Registers a handler to run whenever a message of this command type arrives.
    public void subscribe(TLCCommand command, CommandHandler handler) {
        handlers.computeIfAbsent(command, c -> new ArrayList<>()).add(handler);
    }

    private void onRawLine(String rawLine) {
        InstructionMessage message;
        try {
            message = InstructionMessage.parse(rawLine);
        } catch (IllegalArgumentException e) {
            // Don't crash the receiver thread on one bad line — log and keep going.
            System.err.println("[InstructionReceiver] Dropped malformed message: " + e.getMessage());
            return;
        }

        List<CommandHandler> subscribed = handlers.get(message.getCommand());
        if (subscribed == null || subscribed.isEmpty()) {
            System.err.println("[InstructionReceiver] No handler registered for: " + message.getCommand());
            return;
        }
        for (CommandHandler handler : subscribed) {
            handler.handle(message);
        }
    }
}
*/
