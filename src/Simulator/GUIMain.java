package Simulator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.Random;

/*
creates all javafx visuals and animations
 */
public class GUIMain{
    private final Stage primaryStage;
    private Pane streetPane;

    //window dimensions
    private final double WINDOW_WIDTH;
    private final double WINDOW_HEIGHT;

    //distance between dashes
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

    //store traffic light visuals
    private final ArrayList<TrafficLightVisual> trafficLights = new ArrayList<>();
    private final ArrayList<PedLightVisual> pedLights = new ArrayList<>();


    //store all cars in the simulation
    private final ArrayList<CarVisual> cars = new ArrayList<>();

    //list of car images
    private final List<String> carImages = List.of(
            "/Audi.png",
            "/Black_viper.png",
            "/Car.png",
            "/Mini_truck.png",
            "/Mini_van.png",
            "/Police.png",
            "/taxi.png",
            "/truck.png"
    );

    //1 = low traffic, 10 = heavy traffic
    private int traffic = 0; //traffic off by default

    private Timeline carSpawner;
    private int nextCarID = 0;

    private final Random random = new Random();

    private static final double MIN_CAR_SPEED = 1.0;
    private static final double MAX_CAR_SPEED = 4.0;

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
        makeControls();


        BorderPane root = new BorderPane();

        root.setCenter(streetPane);
        root.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));

        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(root, WINDOW_WIDTH,WINDOW_HEIGHT));
        primaryStage.show();
    }

    //creates the intersection
    private void createIntersection() {
        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2; //left edge
        double intersectionRight = intersectionLeft + ROAD_WIDTH; //right edge
        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2; //top edge
        double intersectionBottom = intersectionTop + ROAD_WIDTH; //bottom edge

        drawRoad(WINDOW_WIDTH, WINDOW_HEIGHT, true);
        drawRoad(WINDOW_HEIGHT, WINDOW_WIDTH, false);

        drawHorizontalLanes();
        drawVerticalLanes();

        drawStopLines();

        drawCrosswalks();

        //northbound traffic lights
        for(int lane = 0; lane < LANES_PER_DIRECTION; lane++) {
            double x = intersectionLeft + (lane * LANE_WIDTH) + (ROAD_WIDTH / 2);
            double y = intersectionBottom + LINE_LENGTH - (STOPLINE_WIDTH * 3) + CROSSWALK_OFFSET;

            drawTrafficLight(x, y, Bearing.North);
        }

        //southbound traffic lights
        for(int lane = 0; lane < LANES_PER_DIRECTION; lane++) {
            double x = intersectionLeft + (lane * LANE_WIDTH);
            double y = intersectionTop - LINE_LENGTH + STOPLINE_WIDTH;

            drawTrafficLight(x, y, Bearing.South);
        }

        //westbound traffic lights
        for(int lane = 0; lane < LANES_PER_DIRECTION; lane++) {
            double x = intersectionRight + LINE_LENGTH - (STOPLINE_WIDTH * 3) + CROSSWALK_OFFSET;
            double y = intersectionTop + (lane * LANE_WIDTH);

            drawTrafficLight(x, y, Bearing.West);
        }

        //eastbound traffic lights
        for(int lane = 0; lane < LANES_PER_DIRECTION; lane++) {
            double x = intersectionLeft - LINE_LENGTH + STOPLINE_WIDTH;
            double y = intersectionTop + (lane * LANE_WIDTH) + (ROAD_WIDTH / 2);

            drawTrafficLight(x, y, Bearing.East);
        }


        double x = intersectionLeft - 25;
        double y = intersectionBottom + 2*CROSSWALK_HEIGHT;
        drawPedLight(x, y, Bearing.North);

        x = intersectionLeft -25;
        y = intersectionTop - 3*CROSSWALK_HEIGHT;
        drawPedLight(x, y, Bearing.South);

        x = intersectionRight;
        y = intersectionTop - 3*CROSSWALK_HEIGHT;
        drawPedLight(x, y, Bearing.West);

        x = intersectionLeft - 25;
        y = intersectionTop - 3*CROSSWALK_HEIGHT;
        drawPedLight(x, y, Bearing.East);


        drawArrowMarkings();

        startCarSpawner();
    }

    private void makeControls() {
        HBox controls = new HBox(5);

        Label trafficLabel = new Label("Traffic");

        Slider trafficSlider = new Slider(0, 10, traffic);

        Button spawnCarButton = new Button("Spawn Car");
        Button spawnEMSButton = new Button("Spawn EMS");

        Button clearAllCars = new Button("Clear All Cars");

        trafficSlider.setShowTickLabels(true);
        trafficSlider.setShowTickMarks(true);
        trafficSlider.setMajorTickUnit(1);
        trafficSlider.setMinorTickCount(0);
        trafficSlider.setSnapToTicks(true);

        trafficSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            setTraffic(newValue.intValue());
        });

        spawnCarButton.setOnAction(event -> {
            spawnCar(false);
        });

        spawnEMSButton.setOnAction(event -> {
            spawnCar(true);
        });

        clearAllCars.setOnAction(event -> {
            for(CarVisual car : new ArrayList<>(cars)) {
                removeCar(car);
            }
        });

        controls.getChildren().addAll(
                trafficLabel,
                trafficSlider,
                spawnCarButton,
                spawnEMSButton,
                clearAllCars
        );

        controls.setLayoutX(20);
        controls.setLayoutY(WINDOW_HEIGHT - 120);

        streetPane.getChildren().add(controls);

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
        for(double y = intersectionTop; y < intersectionTop + ROAD_WIDTH; y += CROSSWALK_GAP) {
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

    //lane markings
    private void drawArrowMarkings() {
        for (Bearing bearing : Bearing.values()) {
            for(int i = 0; i < LANES_PER_DIRECTION; i++) {
                drawArrow(bearing, i);
            }

        }
    }

    //draw arrows
    private void drawArrow(Bearing bearing, int laneNumber) {
        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2;
        double intersectionRight = intersectionLeft + ROAD_WIDTH;

        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2;
        double intersectionBottom = intersectionTop + ROAD_WIDTH;

        double x = 0;
        double y = 0;

        double rotation = 0;

        Group arrow;

        if(laneNumber == 0) {
            arrow = createLeftTurnArrow();
        }

        else if(laneNumber == 2) {
            arrow = createRightTurnArrow();
        }

        else {
            arrow = createStraightArrow();
        }

        //position and rotate the arrows based on the direction of travel
        switch(bearing) {
            case North:
                x = intersectionLeft + (LANES_PER_DIRECTION + laneNumber) * LANE_WIDTH + LANE_WIDTH / 2.0;
                y = intersectionBottom + CROSSWALK_WIDTH + CROSSWALK_OFFSET + STOPLINE_WIDTH * 3;

                rotation = 0;
                break;

            case South:
                x = intersectionLeft + (LANES_PER_DIRECTION - 1 - laneNumber) * LANE_WIDTH + LANE_WIDTH / 2.0;
                y = intersectionTop - CROSSWALK_WIDTH - CROSSWALK_OFFSET - STOPLINE_WIDTH * 3;

                rotation = 180;
                break;

            case East:
                x = intersectionLeft - CROSSWALK_WIDTH - CROSSWALK_OFFSET - STOPLINE_WIDTH * 3;
                y = intersectionTop + (LANES_PER_DIRECTION + laneNumber) * LANE_WIDTH + LANE_WIDTH / 2.0;

                rotation = 90;
                break;

            case West:
                x = intersectionRight + CROSSWALK_WIDTH + CROSSWALK_OFFSET + STOPLINE_WIDTH * 3;
                y = intersectionTop + (LANES_PER_DIRECTION - 1 - laneNumber) * LANE_WIDTH +  LANE_WIDTH / 2.0;

                rotation = 270;
                break;
        }
        //pivot the rotation around (0, 0)
        arrow.getTransforms().add(new javafx.scene.transform.Rotate(rotation, 0, 0));

        arrow.setLayoutX(x);
        arrow.setLayoutY(y);

        streetPane.getChildren().add(arrow);
    }

    //left turn arrow
    private Group createLeftTurnArrow(){
        Group arrow = new Group();

        //base
        Line base = new Line(0, LANE_WIDTH / 1.5, 0, 0);

        //arrow
        Line turn = new Line(0, 0, -LANE_WIDTH / 2.4, 0);

        //arrow head
        Line leftHead = new Line(-LANE_WIDTH / 2.4, 0, -LANE_WIDTH / 7.5, -LANE_WIDTH / 6.0);
        Line rightHead = new Line(-LANE_WIDTH / 2.4, 0, -LANE_WIDTH / 7.5, LANE_WIDTH / 6.0);

        base.setStroke(Color.WHITE);
        turn.setStroke(Color.WHITE);
        leftHead.setStroke(Color.WHITE);
        rightHead.setStroke(Color.WHITE);

        base.setStrokeWidth(LANE_WIDTH / 12.0);
        turn.setStrokeWidth(LANE_WIDTH / 12.0);
        leftHead.setStrokeWidth(LANE_WIDTH / 12.0);
        rightHead.setStrokeWidth(LANE_WIDTH / 12.0);

        arrow.getChildren().addAll(
                base,
                turn,
                leftHead,
                rightHead
        );

        return arrow;
    }

    //straight arrow
    private Group createStraightArrow() {
        Group arrow = new Group();

        //base
        Line base = new Line(0, LANE_WIDTH / 1.5, 0, 0);

        //arrow head
        Line leftHead = new Line(0, 0, -LANE_WIDTH / 6.0, LANE_WIDTH / 7.5);
        Line rightHead = new Line(0, 0, LANE_WIDTH / 6.0, LANE_WIDTH / 7.5);

        base.setStroke(Color.WHITE);
        leftHead.setStroke(Color.WHITE);
        rightHead.setStroke(Color.WHITE);

        base.setStrokeWidth(LANE_WIDTH / 12.0);
        leftHead.setStrokeWidth(LANE_WIDTH / 12.0);
        rightHead.setStrokeWidth(LANE_WIDTH / 12.0);

        arrow.getChildren().addAll(
                base,
                leftHead,
                rightHead
        );

        return arrow;
    }

    //right turn arrow
    private Group createRightTurnArrow(){
        Group arrow = new Group();

        //base
        Line base = new Line(0, LANE_WIDTH / 1.5, 0, 0);

        //arrow
        Line turn = new Line(0, 0, LANE_WIDTH / 2.4, 0);

        //arrow head
        Line leftHead = new Line(LANE_WIDTH / 2.4, 0, LANE_WIDTH / 7.5, -LANE_WIDTH / 6.0);
        Line rightHead = new Line(LANE_WIDTH / 2.4, 0, LANE_WIDTH / 7.5, LANE_WIDTH / 6.0);

        base.setStroke(Color.WHITE);
        turn.setStroke(Color.WHITE);
        leftHead.setStroke(Color.WHITE);
        rightHead.setStroke(Color.WHITE);

        base.setStrokeWidth(LANE_WIDTH / 12.0);
        turn.setStrokeWidth(LANE_WIDTH / 12.0);
        leftHead.setStrokeWidth(LANE_WIDTH / 12.0);
        rightHead.setStrokeWidth(LANE_WIDTH / 12.0);

        arrow.getChildren().addAll(
                base,
                turn,
                leftHead,
                rightHead
        );

        return arrow;
    }

    private void drawPedLight(double x, double y, Bearing bearing){
        double width = 25;
        Rectangle housing;
        Rectangle inner;
        Text timer;

        housing = new Rectangle(x , y, width, width);
        housing.setArcWidth(housing.getWidth() * 0.8);
        housing.setArcHeight(housing.getHeight() * 0.8);
        housing.setFill(Color.DARKGRAY);

        inner = new Rectangle(x + width*.15 , y + width*.15, width*.7, width*.7);
        inner.setFill(Color.GRAY);
        inner.setArcHeight(inner.getHeight()*.8);
        inner.setArcWidth(inner.getWidth()*.8);

        timer = new Text("0");
        timer.setFill(Color.ORANGERED);

        streetPane.getChildren().addAll(housing, inner, timer);
        pedLights.add(new PedLightVisual(timer));
    }

    //traffic light
    private void drawTrafficLight(double x, double y, Bearing bearing) {
        //size of housing
        double height = 25;

        //gap between the lights and housing
        double gap = 0.6;

        //light radius
        double radius = height * 0.4;

        //light circumference
        double circumference = 2 * radius;

        Rectangle housing;

        Circle redLight;
        Circle yellowLight;
        Circle greenLight;

        if(bearing == Bearing.East || bearing == Bearing.West) {
            //housing for lights
            housing = new Rectangle(x, y, height, LANE_WIDTH);

            //round the corners
            housing.setArcWidth(housing.getWidth() * 0.8);
            housing.setArcHeight(housing.getHeight() * 0.3333);

            //traffic lights
            if(bearing == Bearing.West) {
                redLight = new Circle(x + 12.5, y + 10 + (2 * circumference) + gap, radius);
                greenLight = new Circle(x + 12.5, y + 10 + gap, radius);
            }

            else {
                redLight = new Circle(x + 12.5, y + 10 + gap, radius);
                greenLight = new Circle(x + 12.5, y + 10 + (2 * circumference) + gap, radius);
            }

            yellowLight = new Circle(x + 12.5, y + 10 + circumference + gap, radius);

            //inactiveColors
            Color inactiveRed = Color.rgb(80, 20, 20);
            Color inactiveYellow = Color.rgb(80, 70, 20);
            Color inactiveGreen = Color.rgb(20, 70, 30);

            //initial colors
            redLight.setFill(Color.RED);
            yellowLight.setFill(inactiveYellow);
            greenLight.setFill(inactiveGreen);
        }

        else {
            //housing for lights
            housing = new Rectangle(x, y, LANE_WIDTH, height);

            //round the corners
            housing.setArcWidth(housing.getWidth() * 0.3333);
            housing.setArcHeight(housing.getHeight() * 0.8);

            //traffic lights
            if(bearing == Bearing.South) {
                redLight = new Circle(x + 10 + (2 * circumference) + gap, y + 12.5, radius);
                greenLight = new Circle(x + 10 + gap, y + 12.5, radius);
            }

            else {
                redLight = new Circle(x + 10 + gap, y + 12.5, radius);
                greenLight = new Circle(x + 10 + (2 * circumference) + gap, y + 12.5, radius);
            }

            yellowLight = new Circle(x + 10 + circumference + gap, y + 12.5, radius);

            //inactiveColors
            Color inactiveRed = Color.rgb(80, 20, 20);
            Color inactiveYellow = Color.rgb(80, 70, 20);
            Color inactiveGreen = Color.rgb(20, 70, 30);

            //initial colors
            redLight.setFill(Color.RED);
            yellowLight.setFill(inactiveYellow);
            greenLight.setFill(inactiveGreen);
        }

        housing.setFill(Color.BLACK);

        streetPane.getChildren().addAll(housing, redLight, yellowLight, greenLight);

        //store traffic lights to change them later
        trafficLights.add(new TrafficLightVisual(redLight, yellowLight, greenLight));

    }

    public void changeLight(int LaneID, int LightID, LightCol Color){
        GUILane theLane = LaneList.get(LaneID);
        theLane.updateLights(LightID, Color);
    }

    //change traffic light colors
    public void changeTrafficLight(int lightID, LightCol color) {
        //inactiveColors
        Color inactiveRed = Color.rgb(80, 20, 20);
        Color inactiveYellow = Color.rgb(80, 70, 20);
        Color inactiveGreen = Color.rgb(20, 70, 30);

        TrafficLightVisual light = trafficLights.get(lightID);

        //turn off all lights
        light.redLight.setFill(inactiveRed);
        light.yellowLight.setFill(inactiveYellow);
        light.greenLight.setFill(inactiveGreen);

        //turn on requested light
        switch(color) {
            case Red:
                light.redLight.setFill(Color.RED);
                break;

            case Yellow:
                light.yellowLight.setFill(Color.YELLOW);
                break;

            case Green:
                light.greenLight.setFill(Color.GREEN);
                break;
        }
    }

    private void createCar(int id, GUILane lane, Bearing bearing, int laneNumber, boolean EMS) {
        //create the logic car
        GUICar guiCar = new GUICar(id, lane, bearing, laneNumber, null); //TODO will need to standardise laneNumber
        Image image;

        if(EMS) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Ambulance.png")));
        }

        else {
            //create visual car
            String imagePath = carImages.get(random.nextInt(carImages.size()));
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        }

        ImageView car = new ImageView(image);

        car.setFitWidth(LANE_WIDTH);
        car.setFitHeight(LANE_WIDTH);

        positionCar(car, bearing, laneNumber);

        streetPane.getChildren().add(car);
        System.out.println("car has been created.");

        //random speed
        double speed = MIN_CAR_SPEED + random.nextDouble() * (MAX_CAR_SPEED - MIN_CAR_SPEED);

        //connect logic car with visual
        CarVisual carVisual = new CarVisual(guiCar, car, speed);

        //store car
        cars.add(carVisual);

        moveCar(carVisual);
    }

    //remove car
    private void removeCar(CarVisual carVisual) {
        //stop animation
        if(carVisual.getTimeline() != null) {
            carVisual.getTimeline().stop();
        }

        streetPane.getChildren().remove(carVisual.getImageView());
        cars.remove(carVisual);
    }

    //initial position
    //lane number starts at 0, left to right
    private void positionCar(ImageView car, Bearing bearing, int laneNumber) {
        if(laneNumber >= LANES_PER_DIRECTION) {
            return;
        }

        //vertical road
        double roadLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2;

        //horizontal road
        double roadTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2;

        //calculate where the car sits on the road based on lane number and bearing
        switch (bearing) {

            case North:
                //car travels upward
                car.setX(roadLeft + (LANES_PER_DIRECTION + laneNumber) * LANE_WIDTH);
                car.setY(WINDOW_HEIGHT);
                break;


            case South:
                //car travels downward
                car.setX(roadLeft + (LANES_PER_DIRECTION - 1 - laneNumber) * LANE_WIDTH);
                car.setY(-LANE_WIDTH);

                car.setRotate(180);
                break;


            case East:
                //car travels right
                car.setX(-LANE_WIDTH);
                car.setY(roadTop + (LANES_PER_DIRECTION + laneNumber) * LANE_WIDTH);

                car.setRotate(90);
                break;


            case West:
                //car travels left
                car.setX(WINDOW_WIDTH);
                car.setY(roadTop + (LANES_PER_DIRECTION - 1 - laneNumber) * LANE_WIDTH);
                car.setRotate(270);
                break;
        }
    }

    //animation to move the car
    private void moveCar(CarVisual carVisual) {

        ImageView car = carVisual.imageView;
        Bearing bearing = carVisual.car.getBearing();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            double speed = carVisual.getSpeed(); //pixels per frame

            switch(bearing) {
                case North:
                    car.setY(car.getY() - speed);
                    break;

                case South:
                    car.setY(car.getY() + speed);
                    break;

                case East:
                    car.setX(car.getX() + speed);
                    break;

                case West:
                    car.setX(car.getX() - speed);
                    break;
            }

            CarVisual otherCar = isCollidingWithAnotherCar(carVisual);

            if(otherCar != null) {
                System.out.println("Collision!!!");
                carVisual.getTimeline().stop();
                otherCar.getTimeline().stop();
            }

            if(isOutsideScreen(car, bearing)) {
                removeCar(carVisual);
                System.out.println("car has been removed.");
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        //store timeline for car
        carVisual.setTimeline(timeline);

        timeline.play();
    }

    //returns true if a car is outside the screen
    private boolean isOutsideScreen(ImageView car, Bearing bearing) {

        return switch (bearing) {
            case North -> car.getY() + car.getFitHeight() < 0;
            case South -> car.getY() > WINDOW_HEIGHT;
            case East -> car.getX() > WINDOW_WIDTH;
            case West -> car.getX() + car.getFitWidth() < 0;
        };

    }

    //starts spawning car
    private void startCarSpawner() {
        if(traffic <= 0 || traffic > 10) {
            return;
        }

        //traffic = 1; 3000 ms between cars
        //traffic = 10; 750 ms between cars
        double spawnInterval = 3000.0 - (traffic - 1) * 250.0;

        carSpawner = new Timeline(new KeyFrame(Duration.millis(spawnInterval), event -> {
            spawnCar(false);
        }));

        carSpawner.setCycleCount(Timeline.INDEFINITE);

        carSpawner.play();
    }

    //set traffic
    private void setTraffic(int traffic) {
        if (traffic < 0 || traffic > 10) {
            return;
        }

        this.traffic = traffic;

        if (carSpawner != null) {
            carSpawner.stop();
        }

        if(traffic > 0) {
            startCarSpawner();
        }

    }

    //spawns cars in random directions and lanes
    private void spawnCar(boolean EMS) {
        //random direction
        Bearing bearing = Bearing.values()[random.nextInt(Bearing.values().length)];

        //random lane
        //0, 1, or 2
        int laneNumber = random.nextInt(LANES_PER_DIRECTION);

        GUILane lane = null; //null for now

        createCar(nextCarID, lane, bearing, laneNumber, EMS);

        nextCarID++;
    }

    //returns true if two cars are colliding
    private boolean isColliding(CarVisual car1, CarVisual car2) {

        Bounds bounds1 = car1.getImageView().getBoundsInParent();
        Bounds bounds2 = car2.getImageView().getBoundsInParent();

        double padding = 25;

        Bounds smallerBounds1;
        Bounds smallerBounds2;

        Bearing bearing1 = car1.getCurrentBearing();
        Bearing bearing2 = car2.getCurrentBearing();

        //I cant get the hit boxes to be perfect so if anyone wants to
        //tweak the values, that'll be great :)
        if (bearing1 == Bearing.North || bearing1 == Bearing.South) {

            smallerBounds1 = new BoundingBox(
                    bounds1.getMinX() + padding,
                    bounds1.getMinY() + 10,
                    bounds1.getWidth() - padding * 2,
                    bounds1.getHeight() - 10
            );

        }

        else {

            smallerBounds1 = new BoundingBox(
                    bounds1.getMinX(),
                    bounds1.getMinY() + padding,
                    bounds1.getWidth() - 10,
                    bounds1.getHeight() - padding * 2
            );
        }

        if (bearing2 == Bearing.North || bearing2 == Bearing.South) {

            smallerBounds2 = new BoundingBox(
                    bounds2.getMinX() + padding,
                    bounds2.getMinY() + 10,
                    bounds2.getWidth() - padding * 2,
                    bounds2.getHeight() - 10
            );

        }

        else {

            smallerBounds2 = new BoundingBox(
                    bounds2.getMinX(),
                    bounds2.getMinY() + padding,
                    bounds2.getWidth() - 10,
                    bounds2.getHeight() - padding * 2
            );
        }

        return smallerBounds1.intersects(smallerBounds2);
    }

    //collision with a car
    private CarVisual isCollidingWithAnotherCar(CarVisual car) {
        for(CarVisual otherCar : cars) {
            //don't check against itself
            if(otherCar == car) {
                continue;
            }

            if(isColliding(car, otherCar)) {
                return otherCar;
            }
        }

        //no collision
        return null;
    }

    //small private helper class to store traffic lights
    private record TrafficLightVisual(Circle redLight, Circle yellowLight, Circle greenLight) {
    }
    private record PedLightVisual (Text timer) {}

    //small private helper class to store car visuals
    private static class CarVisual {
        private final GUICar car;
        private final ImageView imageView;
        private final double speed;
        private Timeline timeline;

        private Bearing currentBearing;

        public CarVisual(GUICar car, ImageView imageView, double speed) {
            this.car = car;
            this.imageView = imageView;
            this.speed = speed;

            this.currentBearing = car.getBearing();
        }

        public Bearing getCurrentBearing() {
            return currentBearing;
        }

        public void setCurrentBearing(Bearing currentBearing) {
            this.currentBearing = currentBearing;
        }

        public GUICar getCar() {
            return car;
        }

        public double getSpeed() {
            return speed;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public Timeline getTimeline() {
            return timeline;
        }

        public void setTimeline(Timeline timeline) {
            this.timeline = timeline;
        }
    }
}
