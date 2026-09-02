package Communication;

/**
 * Standalone demo of the instruction layer, using FakeNetworkChannel instead
 * of the real Harness Socket and plain println() instead of the real
 * JavaFX animation controller. This deliberately has ZERO dependency on
 * JavaFX or the real socket, so anyone on the team can compile and run it
 * with just a plain JDK — no project setup required — to see the whole
 * send -> wire format -> parse -> dispatch pipeline actually working.
 *
 * Run with:
 *   javac tlc/protocol/*.java -d out
 *   java -cp out tlc.protocol.TLCDemo
 */

/*
public class TLCDemo {

    public static void main(String[] args) {
        FakeNetworkChannel channel = new FakeNetworkChannel();
        InstructionSender sender = new InstructionSender(channel);
        InstructionReceiver receiver = new InstructionReceiver(channel);

        // --- Pretend to be the animation/GUI side subscribing to inbound commands ---
        receiver.subscribe(TLCCommand.SET_LIGHT_STATE, msg ->
                System.out.println("  [ANIMATION] Light " + msg.getTarget() + " -> " + msg.getValue()));

        receiver.subscribe(TLCCommand.UPDATE_PEDESTRIAN_SIGNAL, msg ->
                System.out.println("  [ANIMATION] Pedestrian signal " + msg.getTarget() + " -> " + msg.getValue()));

        receiver.subscribe(TLCCommand.TRIGGER_FAIL_SAFE, msg ->
                System.out.println("  [ANIMATION] FAIL-SAFE engaged: " + msg.getValue()));

        receiver.subscribe(TLCCommand.RESUME_NORMAL, msg ->
                System.out.println("  [ANIMATION] Resuming normal operation"));

        System.out.println("=== 1. Outbound: simulated hardware events -> wire messages ===");
        sender.sendPedestrianButtonPressed("NORTH");       // crossing id
        sender.sendVehicleDetected("7");                   // light/lane id 1-12
        sender.sendEmsPriorityRequest("NORTH");             // approach direction

        System.out.println("\n=== 2. Inbound: pretend these lines just arrived from the server ===");
        channel.simulateIncoming("SET_LIGHT_STATE:7:GREEN:1735599999000");
        channel.simulateIncoming("UPDATE_PEDESTRIAN_SIGNAL:NORTH:WALK:1735600001000");
        channel.simulateIncoming("TRIGGER_FAIL_SAFE:ALL:FLASH_RED:1735600005000");
        channel.simulateIncoming("RESUME_NORMAL:ALL:NORMAL:1735600010000");

        System.out.println("\n=== 3. Malformed line -> logged and dropped, no crash ===");
        channel.simulateIncoming("NOT_A_REAL_COMMAND:x:y:z");

        System.out.println("\nDone. " + channel.getSentLines().size() + " outbound message(s) were sent.");
    }
}
*/
