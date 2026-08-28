import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;


public class GUIHub{
    private Stage primaryStage;
    private ArrayList<GUILane> LaneList = new ArrayList<>();
    private ArrayList<GUICar> CarList = new ArrayList<>();


    public GUIHub(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void makeGUI() {
        primaryStage.setTitle("TLC Team9");

    }

    public void changeLight(int LaneID, int, LightID, LightCol Color){
        GUILane theLane = LaneList.get(LaneID);
        theLane.
    }
}