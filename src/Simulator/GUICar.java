package Simulator;

public class GUICar implements Runnable {
    private GUILane myLane;
    private Bearing myBearing;
    private int ID;
    private boolean isAlive = true;
    private int laneID;
    private int distance;

    public GUICar(int ID, GUILane myLane, Bearing myBearing, int laneID){
        this.ID = ID;
        this.myLane = myLane;
        this.myBearing = myBearing;
        this.distance = 0;
        this.laneID = laneID;
    }

    public Bearing getBearing() {
        return myBearing;
    }

    @Override
    public void run() {
        while (isAlive) {
            try{
                if(distance == 50){
                    myLane.updateSensor(laneID, true);

                    if(myLane.getLightCol(laneID) == LightCol.Green){
                        //GO
                    }else{
                        //DON'T GO
                    }
                }
                if(distance == 51){
                    myLane.updateSensor(laneID, false);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}