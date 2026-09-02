package Communication;

/**
 * Example wiring class — this is the piece that actually connects the
 * protocol layer to teammate #3's JavaFX animation code. You'll likely
 * replace TrafficLightAnimationController with their real class/interface,
 * but keeping it as an interface here means you can build and test your
 * whole layer before their animation code even exists.
 *
 * IMPORTANT — threading: the Harness Socket reads incoming lines on a
 * background (non-JavaFX) thread, so InstructionReceiver's callbacks also
 * fire on that thread. JavaFX forbids touching the scene graph from any
 * thread other than the JavaFX Application Thread, so every call into the
 * animation controller is wrapped in Platform.runLater(...) here. Without
 * this, the app throws "Not on FX application thread" as soon as a real
 * message arrives from the real socket.
 */
/*
public class GUIControllerBridge {

    public GUIControllerBridge(InstructionReceiver receiver,
                                TrafficLightAnimationController animationController) {

        receiver.subscribe(TLCCommand.SET_LIGHT_STATE, msg ->
                Platform.runLater(() ->
                        animationController.setLightState(msg.getTarget(), msg.getValue())));

        receiver.subscribe(TLCCommand.UPDATE_PEDESTRIAN_SIGNAL, msg ->
                Platform.runLater(() ->
                        animationController.setPedestrianSignal(msg.getTarget(), msg.getValue())));

        receiver.subscribe(TLCCommand.TRIGGER_FAIL_SAFE, msg ->
                Platform.runLater(() ->
                        animationController.enterFailSafeMode(msg.getValue())));

        receiver.subscribe(TLCCommand.RESUME_NORMAL, msg ->
                Platform.runLater(animationController::resumeNormalOperation));
    }

    /**
     * Placeholder for teammate #3's real JavaFX drawing/animation class.
     * Ask them to implement this (or adapt these method names to match
     * whatever they actually expose) — your code only needs the interface.

    public interface TrafficLightAnimationController {
        void setLightState(String laneOrDirectionId, String state);   // state: RED/YELLOW/GREEN
        void setPedestrianSignal(String crossingId, String state);    // state: WALK/WAIT
        void enterFailSafeMode(String flashMode);                     // FLASH_RED/FLASH_YELLOW
        void resumeNormalOperation();
    }
}
*/