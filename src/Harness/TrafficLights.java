package Harness;

public class TrafficLights {
    private Mux mux;
    public TrafficLights(Mux mux) {
        this.mux = mux;
    }

    public void setLightPattern(LightPattern pattern) {
        // translate lightpattern into instructionMessage to send as command through mux
    }
}
