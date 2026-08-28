package Simulator;

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


public class GUIMain{
    private Stage primaryStage;
    private ArrayList<GUILane> LaneList = new ArrayList<>();
    private ArrayList<GUICar> CarList = new ArrayList<>();


    public GUIMain(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void makeGUI() {
        primaryStage.setTitle("TLC Team9");
        Pane streetPane = new Pane();
        streetPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        streetPane.setMinSize(1200, 700);
        streetPane.setMaxSize(1200, 700);


        BorderPane root = new BorderPane();
        root.setCenter(streetPane);
        root.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
        primaryStage.setScene(new Scene(root, 1200,700));
        primaryStage.show();
    }

    public void changeLight(int LaneID, int LightID, LightCol Color){
        GUILane theLane = LaneList.get(LaneID);
        theLane.updateLights(LightID, Color);
    }
}