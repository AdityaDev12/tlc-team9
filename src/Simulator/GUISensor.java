package Simulator;

public class GUISensor {
    private int ID;
    private Boolean active = false;
    public GUISensor(int ID)
    {
        this.ID = ID;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }

    public int getID(){
        return ID;
    }
}
