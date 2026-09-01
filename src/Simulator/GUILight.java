package Simulator;

public class GUILight {
    private int ID;
    private LightCol Color = LightCol.Red;
    private LightShape Shape;
    private Bearing Direction;

    public GUILight(int ID, LightShape Shape, Bearing Direction) {
        this.ID = ID;
        this.Shape = Shape;
        this.Direction = Direction;
    }
    public void changeColor(LightCol Color){
        this.Color = Color;

    }

    public LightCol getColor() {
        return Color;
    }

    public int getID() {
        return ID;
    }

    public LightShape getShape() {
        return Shape;
    }

    public Bearing getDirection() {
        return Direction;
    }
}