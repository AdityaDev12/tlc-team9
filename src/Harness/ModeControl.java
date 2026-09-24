package Harness;

public class ModeControl {
    private DayMode dayMode;
    private NightMode nightMode;
    private EMSMode emsMode;
    private PedestrianMode pedestrianMode;
    private Clock clock;

    // conditions
    private boolean pedRequest;
    private boolean emsRequest;

    public ModeControl(
            DayMode dayMode,
            NightMode nightMode,
            EMSMode emsMode,
            PedestrianMode pedestrianMode,
            Clock clock) {
        this.dayMode = dayMode;
        this.nightMode = nightMode;
        this.emsMode = emsMode;
        this.pedestrianMode = pedestrianMode;
        this.clock = clock;

        pedRequest = false;
        emsRequest = false;

    }
    public void setPedRequest(boolean request) {
        pedRequest = request;
    }
    public void setEmsRequest(boolean request) {
        emsRequest = request;
    }
}
