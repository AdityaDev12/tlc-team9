package Protocol;

/**
 * Outbound half of your responsibility: GUI/simulator event -> wire message.
 *
 * This is a thin Command-pattern layer: each public method here builds one
 * InstructionMessage "command object" and hands it to the NetworkChannel.
 * The GUI/animation code and the sensor-simulation code should call these
 * methods directly instead of ever building raw strings themselves that
 * keeps the wire format in exactly one place (InstructionMessage).
 */
public class InstructionSender {

    private final NetworkChannel channel;

    public InstructionSender(NetworkChannel channel) {
        this.channel = channel;
    }

    public void sendVehicleDetected(String laneId) {
        send(InstructionMessage.of(TLCCommand.VEHICLE_DETECTED, laneId, "DETECTED"));
    }

    public void sendVehicleCleared(String laneId) {
        send(InstructionMessage.of(TLCCommand.VEHICLE_CLEARED, laneId, "CLEARED"));
    }

    public void sendPedestrianButtonPressed(String crossingId) {
        send(InstructionMessage.of(TLCCommand.PEDESTRIAN_BUTTON_PRESSED, crossingId, "PRESSED"));
    }

    public void sendEmsPriorityRequest(String direction) {
        send(InstructionMessage.of(TLCCommand.EMS_PRIORITY_REQUEST, direction, "REQUEST"));
    }

    public void sendEmsPriorityCancel(String direction) {
        send(InstructionMessage.of(TLCCommand.EMS_PRIORITY_CANCEL, direction, "CANCEL"));
    }

    private void send(InstructionMessage message) {
        channel.sendLine(message.toWireFormat());
    }
}
