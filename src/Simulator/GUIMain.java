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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;


public class GUIMain{
    private final Stage primaryStage;
    private Pane streetPane;

    //window dimensions
    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 700;

    //road dimensions
    private static final int LANES_PER_DIRECTION = 3;
    private static final int LANE_WIDTH = 40;

    private static final double ROAD_WIDTH = LANES_PER_DIRECTION * 2 * LANE_WIDTH;

    private final ArrayList<GUILane> LaneList = new ArrayList<>();
    private final ArrayList<GUICar> CarList = new ArrayList<>();


    public GUIMain(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void makeGUI() {
        primaryStage.setTitle("TLC Team9");

        streetPane = new Pane();

        streetPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        streetPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        createIntersection();


        BorderPane root = new BorderPane();

        root.setCenter(streetPane);
        root.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));

        primaryStage.setScene(new Scene(root, WINDOW_WIDTH,WINDOW_HEIGHT));
        primaryStage.show();
    }

    //creates the intersection
    private void createIntersection() {
        drawHorizontalRoad();
        drawVerticalRoad();
    }

    //horizontal road
    private void drawHorizontalRoad() {
        double y = (WINDOW_HEIGHT - ROAD_WIDTH) / 2;

        Rectangle horizontalRoad = new Rectangle(0, y, WINDOW_WIDTH, ROAD_WIDTH);

        horizontalRoad.setFill(Color.DARKGRAY);

        streetPane.getChildren().add(horizontalRoad);
    }

    //vertical road
    private void drawVerticalRoad() {
        double x = (WINDOW_WIDTH - ROAD_WIDTH) / 2;

        Rectangle verticalRoad = new Rectangle(x, 0, ROAD_WIDTH, WINDOW_HEIGHT);

        verticalRoad.setFill(Color.DARKGRAY);

        streetPane.getChildren().add(verticalRoad);
    }


    public void changeLight(int LaneID, int LightID, LightCol Color){
        GUILane theLane = LaneList.get(LaneID);
        theLane.updateLights(LightID, Color);
    }
}