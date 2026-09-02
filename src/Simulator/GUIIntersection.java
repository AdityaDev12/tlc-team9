package Simulator;
import java.util.ArrayList;

public class GUIIntersection {
    private ArrayList<ArrayList<Boolean>> intersection = new ArrayList<>();
    private int intersectionSize;

    public GUIIntersection(int intersectionSize) {
        this.intersection = new ArrayList<>();

        makeIntersection(intersectionSize, intersection);
    }

    private void makeIntersection(int intersectionSize, ArrayList<ArrayList<Boolean>> intersection) {
        for (int i = 0; i < intersectionSize; i++) {
            ArrayList<Boolean> row = new ArrayList<>();
            for (int j = 0; j < intersectionSize; j++) {
                row.add(false);
            }
            intersection.add(row);
        }
    }

    public void updateIntersection(int x, int y, boolean active){
        if( intersection.get(x).get(y) == false){
            intersection.get(x).set(y, active);
        }
        if( intersection.get(x).get(y) == true){
            //CRASH
        }
    }
}
