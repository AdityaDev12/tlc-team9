package Simulator;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;


public class GUIMain{
    private final Stage primaryStage;
    private Pane streetPane;

    //window dimensions
    private final double WINDOW_WIDTH;
    private final double WINDOW_HEIGHT;

    // Distance between dashes
    private static final double DASH_LENGTH = 30;
    private static final double GAP_LENGTH = 20;

    //road dimensions
    private static final int LANES_PER_DIRECTION = 3;
    private static final int LANE_WIDTH = 60;

    private static final double ROAD_WIDTH = LANES_PER_DIRECTION * 2 * LANE_WIDTH;

    //distance from intersection where line begins
    private static final double LINE_LENGTH = 100;

    //stop line
    private static final double STOPLINE_WIDTH = 15;

    //crosswalk
    private static final double CROSSWALK_WIDTH = 80;
    private static final double CROSSWALK_HEIGHT = 25;
    private static final double CROSSWALK_GAP = 40;
    private static final double CROSSWALK_OFFSET = 5;

    private final ArrayList<GUILane> LaneList = new ArrayList<>();
    private final ArrayList<GUICar> CarList = new ArrayList<>();


    public GUIMain(Stage primaryStage){
        this.primaryStage = primaryStage;

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        WINDOW_WIDTH = screenBounds.getWidth();
        WINDOW_HEIGHT = screenBounds.getHeight();
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

        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(root, WINDOW_WIDTH,WINDOW_HEIGHT));
        primaryStage.show();
    }

    //creates the intersection
    private void createIntersection() {
        drawRoad(WINDOW_WIDTH, WINDOW_HEIGHT, true);
        drawRoad(WINDOW_HEIGHT, WINDOW_WIDTH, false);

        drawHorizontalLanes();
        drawVerticalLanes();

        drawStopLines();

        drawCrosswalks();
    }

    //draw road
    private void drawRoad(double x, double y, boolean vertical) {
        double pos = (x - ROAD_WIDTH) / 2;

        Rectangle horizontalRoad;

        if(vertical) {
            horizontalRoad = new Rectangle(pos, 0, ROAD_WIDTH, y);
        }

        else {
            horizontalRoad = new Rectangle(0, pos, y, ROAD_WIDTH);
        }

        horizontalRoad.setFill(Color.DARKGRAY);

        streetPane.getChildren().add(horizontalRoad);
    }

    //draw horizontal lanes
    private void drawHorizontalLanes() {
        double horizontalRoad = (WINDOW_HEIGHT - ROAD_WIDTH) / 2; //where the lane starts

        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2; //left edge of the road

        double intersectionRight = intersectionLeft + ROAD_WIDTH; //right edge of the road

        //draw lanes
        //6 lanes total; 5 lines
        for(int i = 1; i < LANES_PER_DIRECTION * 2; i++) {
            double y = horizontalRoad + i * LANE_WIDTH; //calculates the y coordinate of the line

            //center divider
            if(i == LANES_PER_DIRECTION) {
                drawHorizontalCenterDivider(y, 0, intersectionLeft - LINE_LENGTH);
                drawHorizontalCenterDivider(y, intersectionRight + LINE_LENGTH, WINDOW_WIDTH);

                continue;
            }

            //dashed line left side
            drawHorizontalDashedLine(y, 0, intersectionLeft - LINE_LENGTH);

            //dashed line right side
            drawHorizontalDashedLine(y, intersectionRight + LINE_LENGTH, WINDOW_WIDTH);
        }
    }


    //horizontal dashed line
    private void drawHorizontalDashedLine(double y, double startX, double endX) {

        //draw dashed lines until endX
        for (double x = startX; x < endX; x += DASH_LENGTH + GAP_LENGTH) {

            double width = Math.min(DASH_LENGTH, endX - x);

            if (width <= 0) {
                break;
            }

            Rectangle dash = new Rectangle(x, y, width, 2);

            dash.setFill(Color.WHITE);

            streetPane.getChildren().add(dash);
        }
    }


    //solid horizontal line
    private void drawHorizontalCenterDivider(double y, double startX, double endX) {

        Rectangle line = new Rectangle(startX, y, endX - startX, 2);

        line.setFill(Color.YELLOW);

        streetPane.getChildren().add(line);
    }

    //draw horizontal lanes
    private void drawVerticalLanes() {
        double verticalRoad = (WINDOW_WIDTH - ROAD_WIDTH) / 2; //where the lane starts

        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2; //top side of the road

        double intersectionBottom = intersectionTop + ROAD_WIDTH; //bottom side of the road

        //draw lanes
        for(int i = 1; i < LANES_PER_DIRECTION * 2; i++) {
            double x = verticalRoad + i * LANE_WIDTH;

            //center divider
            if(i == LANES_PER_DIRECTION) {
                drawVerticalCenterDivider(x, 0, intersectionTop - LINE_LENGTH);
                drawVerticalCenterDivider(x, intersectionBottom + LINE_LENGTH, WINDOW_HEIGHT);

                continue;
            }

            //dashed line left side
            drawVerticalDashedLine(x, 0, intersectionTop - LINE_LENGTH);

            //dashed line right side
            drawVerticalDashedLine(x, intersectionBottom + LINE_LENGTH, WINDOW_HEIGHT);
        }
    }


    //horizontal dashed line
    private void drawVerticalDashedLine(double x, double startY, double endY) {

        //draw dashed lines
        for (double y = startY; y < endY; y += DASH_LENGTH + GAP_LENGTH) {

            double height = Math.min(DASH_LENGTH, endY - y);

            if (height <= 0) {
                break;
            }

            Rectangle dash = new Rectangle(x, y, 2, height);

            dash.setFill(Color.WHITE);

            streetPane.getChildren().add(dash);
        }
    }


    //solid horizontal line
    private void drawVerticalCenterDivider(double x, double startY, double endY) {

        Rectangle line = new Rectangle(x, startY, 2, endY - startY);

        line.setFill(Color.YELLOW);

        streetPane.getChildren().add(line);
    }

    //stop line
    private void drawStopLines() {
        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2; //left edge
        double intersectionRight = intersectionLeft + ROAD_WIDTH; //right edge
        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2; //top edge
        double intersectionBottom = intersectionTop + ROAD_WIDTH; //bottom edge

        //west stop line
        Rectangle westStopLine = new Rectangle(
                intersectionLeft - LINE_LENGTH,
                intersectionTop + (ROAD_WIDTH / 2),
                STOPLINE_WIDTH,
                ROAD_WIDTH / 2
        );

        westStopLine.setFill(Color.WHITE);

        //east stop line
        Rectangle eastStopLine = new Rectangle(
                intersectionRight + LINE_LENGTH - STOPLINE_WIDTH,
                intersectionTop,
                STOPLINE_WIDTH,
                ROAD_WIDTH / 2
        );

        eastStopLine.setFill(Color.WHITE);

        //north stop line
        Rectangle northStopLine = new Rectangle(
                intersectionLeft,
                intersectionTop - LINE_LENGTH,
                ROAD_WIDTH / 2,
                STOPLINE_WIDTH
        );

        northStopLine.setFill(Color.WHITE);

        //south stop line
        Rectangle southStopLine = new Rectangle(
                intersectionLeft + (ROAD_WIDTH / 2),
                intersectionBottom + LINE_LENGTH - STOPLINE_WIDTH,
                ROAD_WIDTH / 2,
                STOPLINE_WIDTH
        );

        southStopLine.setFill(Color.WHITE);

        streetPane.getChildren().addAll(
                westStopLine,
                eastStopLine,
                northStopLine,
                southStopLine
        );
    }

    //draw crosswalks
    private void drawCrosswalks() {
        drawWestCrosswalk();
        drawEastCrosswalk();
        drawNorthCrosswalk();
        drawSouthCrosswalk();
    }

    //west crosswalk
    private void drawWestCrosswalk() {
        //left edge of intersection
        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2;

        //top edge of intersection
        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2;

        //position of crosswalk before the intersection
        double x = intersectionLeft - LINE_LENGTH + STOPLINE_WIDTH + CROSSWALK_OFFSET;

        //draw horizontal crosswalk stripes
        for(double y = intersectionTop; y < intersectionTop + ROAD_WIDTH; y+= CROSSWALK_GAP) {
            Rectangle stripe = new Rectangle(x, y + CROSSWALK_OFFSET, CROSSWALK_WIDTH, CROSSWALK_HEIGHT);

            stripe.setFill(Color.WHITE);

            streetPane.getChildren().add(stripe);
        }
    }

    //east crosswalk
    private void drawEastCrosswalk() {
        //right edge of intersection
        double intersectionRight = (WINDOW_WIDTH + ROAD_WIDTH) / 2;

        //top edge of intersection
        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2;

        //draw horizontal crosswalk stripes
        for(double y = intersectionTop; y < intersectionTop + ROAD_WIDTH; y += CROSSWALK_GAP) {
            Rectangle stripe = new Rectangle(intersectionRight, y + CROSSWALK_OFFSET, CROSSWALK_WIDTH, CROSSWALK_HEIGHT);

            stripe.setFill(Color.WHITE);

            streetPane.getChildren().add(stripe);
        }
    }

    //north crosswalk
    private void drawNorthCrosswalk() {
        //ledge edge of intersection
        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2;

        //top edge of intersection
        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2;

        //position of crosswalk before intersection
        double y = intersectionTop - LINE_LENGTH + STOPLINE_WIDTH + CROSSWALK_OFFSET;

        //draw vertical crosswalk stripes
        for(double x = intersectionLeft; x < intersectionLeft + ROAD_WIDTH; x += CROSSWALK_GAP) {
            Rectangle stripe = new Rectangle(x + CROSSWALK_OFFSET, y, CROSSWALK_HEIGHT, CROSSWALK_WIDTH);

            stripe.setFill(Color.WHITE);

            streetPane.getChildren().add(stripe);
        }
    }

    //south crosswalk
    private void drawSouthCrosswalk(){
        //ledge edge of intersection
        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2;

        //position of crosswalk before intersection
        double y = (WINDOW_HEIGHT + ROAD_WIDTH) / 2;

        //draw vertical crosswalk stripes
        for(double x = intersectionLeft; x < intersectionLeft + ROAD_WIDTH; x += CROSSWALK_GAP) {
            Rectangle stripe = new Rectangle(x + CROSSWALK_OFFSET, y, CROSSWALK_HEIGHT, CROSSWALK_WIDTH);

            stripe.setFill(Color.WHITE);

            streetPane.getChildren().add(stripe);
        }
    }

    public void changeLight(int LaneID, int LightID, LightCol Color){
        GUILane theLane = LaneList.get(LaneID);
        theLane.updateLights(LightID, Color);
    }
}