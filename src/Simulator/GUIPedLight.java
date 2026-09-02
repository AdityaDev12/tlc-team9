package Simulator;

public class GUIPedLight {
    private final int id;
    private boolean active;
    private int timer;
    private Bearing direction;

    GUIPedLight(int id, boolean active, Bearing direction) {
        this.id = id;
        this.active = active;
        this.direction = direction;
    }
    public int getID() {
        return id;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }
    public int getTimer() {
        return timer;
    }
    public void setTimer(int timer) {
        this.timer = timer;
    }
}
