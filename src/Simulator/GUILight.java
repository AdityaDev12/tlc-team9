package Simulator;

public class GUILight {
    private int ID;
    private LightCol Color;

    public GUILight(int ID) {
        this.ID = ID;
    }

    public void changeColor(LightCol Color){
        this.Color = Color;

    }
}