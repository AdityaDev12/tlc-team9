import java.util.concurrent.*;

public class GUICar implements Runnable {
    private Lane myLane;
    private Bearing myBearing;
    private int ID;
    private boolean isAlive = true;

    public GuiCar(int ID, Lane myLane, Bearing myBearing){
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
            }
        }
    }
}