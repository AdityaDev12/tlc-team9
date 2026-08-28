package Simulator;

public class GUICar implements Runnable {
    private GUILane myLane;
    private Bearing myBearing;
    private int ID;
    private boolean isAlive = true;

    public void GuiCar(int ID, GUILane myLane, Bearing myBearing){
        this.ID = ID;
        this.myLane = myLane;
        this.myBearing = myBearing;
    }

    @Override
    public void run() {
        while (isAlive) {
            try{
                //check light
                //if green go otherwise wait
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}