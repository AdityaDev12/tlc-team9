package Protocol;

/**
 * A callback invoked whenever a message of a subscribed TLCCommand arrives.
 * Because it's a functional interface, you can register handlers as lambdas:
 *
 *   receiver.subscribe(TLCCommand.SET_LIGHT_STATE,
 *       msg -> animationController.setLightState(msg.getTarget(), msg.getValue()));
 */
@FunctionalInterface
public interface CommandHandler {
    void handle(InstructionMessage message);
}
