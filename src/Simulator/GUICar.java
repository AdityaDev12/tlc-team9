package Simulator;

public class GUICar implements Runnable {
    private GUILane myLane;
    private Bearing myBearing;
    private int ID;
    private boolean isAlive = true;

    public GUICar(int ID, GUILane myLane, Bearing myBearing){
        this.ID = ID;
        this.myLane = myLane;
        this.myBearing = myBearing;
    }

    public Bearing getBearing() {
        return myBearing;
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