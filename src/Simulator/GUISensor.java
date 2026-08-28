package Simulator;

public class GUISensor {
    private int ID;
    private Boolean active;
    public GUISensor(int ID)
    {
        this.ID = ID;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }
    public int getID(){
        return ID;
    }
}
