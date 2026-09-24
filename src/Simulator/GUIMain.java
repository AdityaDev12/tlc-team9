package Simulator;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

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

    //store traffic light visuals
    private final Map<Bearing, ArrayList<TrafficLightVisual>> trafficLights = new EnumMap<>(Bearing.class);
    private final ArrayList<PedestrianVisual> pedestrians = new ArrayList<>();

    private final ArrayList<PedLightVisual> pedLights = new ArrayList<>();


    //store all cars in the simulation
    private final ArrayList<CarVisual> cars = new ArrayList<>();

    //list of car images
    private final List<String> carImages = List.of(
            "/cars/Audi.png",
            "/cars/Black_viper.png",
            "/cars/Car.png",
            "/cars/Mini_truck.png",
            "/cars/Mini_van.png",
            "/cars/Police.png",
            "/cars/taxi.png",
            "/cars/truck.png"
    );

    //list of pedestrians
    private final List<List<String>> pedestrianImages = List.of(
            List.of(
                    "/pedestrians/person1_walk1.png",
                    "/pedestrians/person1_walk2.png",
                    "/pedestrians/person1_walk3.png"
            ),

            List.of(
                    "/pedestrians/person3_walk1.png",
                    "/pedestrians/person3_walk2.png",
                    "/pedestrians/person3_walk3.png"
            ),

            List.of(
                    "/pedestrians/person4_walk1.png",
                    "/pedestrians/person4_walk2.png",
                    "/pedestrians/person4_walk3.png"
            )
    );

    //1 = low traffic, 10 = heavy traffic
    private int traffic = 0; //traffic off by default

    private Timeline carSpawner;
    private int nextCarID = 0;

    private final Random random = new Random();

    private static final double MIN_CAR_SPEED = 2.0;
    private static final double MAX_CAR_SPEED = 3.5;

    private Circle emsIndicator;
    private Label emsLabel;

    private boolean pedestrianCanWalk = false;

    public GUIMain(Stage primaryStage){
        this.primaryStage = primaryStage;

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        WINDOW_WIDTH = screenBounds.getWidth();
        WINDOW_HEIGHT = screenBounds.getHeight();

        trafficLights.put(Bearing.North, new ArrayList<>());
        trafficLights.put(Bearing.South, new ArrayList<>());
        trafficLights.put(Bearing.East, new ArrayList<>());
        trafficLights.put(Bearing.West, new ArrayList<>());
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

        createLanes();

        drawHorizontalLanes();
        drawVerticalLanes();

        drawStopLines();

        drawCrosswalks();

        //northbound traffic lights
        for(int lane = 0; lane < LANES_PER_DIRECTION; lane++) {
            double x = intersectionLeft + (lane * LANE_WIDTH) + (LANE_WIDTH / 2.0) + (ROAD_WIDTH / 2);
            double y = intersectionBottom + LINE_LENGTH - (STOPLINE_WIDTH * 3) + CROSSWALK_OFFSET * 3;

            LightShape shape = switch (lane) {
                case 0 -> LightShape.LeftArrow;
                case 2 -> LightShape.RightArrow;
                default -> LightShape.Square;
            };

            drawTrafficLight(x, y, Bearing.North, shape);
        }

        //southbound traffic lights
        for(int lane = 0; lane < LANES_PER_DIRECTION; lane++) {
            double x = intersectionLeft + (lane * LANE_WIDTH) + (LANE_WIDTH / 2.0);
            double y = intersectionTop - LINE_LENGTH + STOPLINE_WIDTH * 2;

            LightShape shape = switch (lane) {
                case 0 -> LightShape.RightArrow;
                case 2 -> LightShape.LeftArrow;
                default -> LightShape.Square;
            };

            drawTrafficLight(x, y, Bearing.South, shape);
        }

        //westbound traffic lights
        for(int lane = 0; lane < LANES_PER_DIRECTION; lane++) {
            double x = intersectionRight + LINE_LENGTH - (STOPLINE_WIDTH * 3) + CROSSWALK_OFFSET * 3;
            double y = intersectionTop + (lane * LANE_WIDTH) + (LANE_WIDTH / 2.0);

            LightShape shape = switch (lane) {
                case 0 -> LightShape.RightArrow;
                case 2 -> LightShape.LeftArrow;
                default -> LightShape.Square;
            };

            drawTrafficLight(x, y, Bearing.West, shape);
        }

        //eastbound traffic lights
        for(int lane = 0; lane < LANES_PER_DIRECTION; lane++) {
            double x = intersectionLeft - LINE_LENGTH + STOPLINE_WIDTH * 2;
            double y = intersectionTop + (lane * LANE_WIDTH) + (ROAD_WIDTH / 2) + (LANE_WIDTH / 2.0);

            LightShape shape = switch (lane) {
                case 0 -> LightShape.LeftArrow;
                case 2 -> LightShape.RightArrow;
                default -> LightShape.Square;
            };

            drawTrafficLight(x, y, Bearing.East, shape);
        }

        drawSensors();


        double x = intersectionLeft - 25;
        double y = intersectionBottom + 2*CROSSWALK_HEIGHT;
        drawPedLight(x, y, Bearing.West);
        x = intersectionLeft - 3*CROSSWALK_HEIGHT;
        y = intersectionBottom;
        drawPedLight(x, y, Bearing.South);

        x = intersectionLeft -25;
        y = intersectionTop - 3*CROSSWALK_HEIGHT;
        drawPedLight(x, y, Bearing.West);
        x = intersectionLeft - 3*CROSSWALK_HEIGHT;
        y = intersectionTop - 25;
        drawPedLight(x, y, Bearing.North);

        x = intersectionRight;
        y = intersectionTop - 3*CROSSWALK_HEIGHT;
        drawPedLight(x, y, Bearing.East);
        x = intersectionRight + 2*CROSSWALK_HEIGHT;
        y = intersectionTop - 25;
        drawPedLight(x, y, Bearing.North);

        x = intersectionRight + 2*CROSSWALK_HEIGHT;
        y = intersectionBottom ;
        drawPedLight(x, y, Bearing.South);
        x = intersectionRight;
        y = intersectionBottom +2*CROSSWALK_HEIGHT ;
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
        Button spawnPedestrian = new Button("Spawn Person");

        Button clearAllCars = new Button("Clear All Cars");

        trafficSlider.setShowTickLabels(true);
        trafficSlider.setShowTickMarks(true);
        trafficSlider.setMajorTickUnit(1);
        trafficSlider.setMinorTickCount(0);
        trafficSlider.setSnapToTicks(true);

        trafficSlider.valueProperty().addListener((obsVal, oldVal, newValue) -> {
            setTraffic(newValue.intValue());
        });

        spawnCarButton.setOnAction(event -> {
            spawnCar(false);

            //cool down between spawns
            spawnCarButton.setDisable(true);

            PauseTransition cooldown = new PauseTransition(Duration.seconds(3));

            cooldown.setOnFinished(nxtEvent -> {
                spawnCarButton.setDisable(false);
            });

            cooldown.play();
        });

        spawnEMSButton.setOnAction(event -> {
            spawnCar(true);
            setEMSIndicator(true);

            //cool down between spawns
            spawnEMSButton.setDisable(true);

            PauseTransition cooldown = new PauseTransition(Duration.seconds(3));

            cooldown.setOnFinished(nxtEvent -> {
                spawnEMSButton.setDisable(false);
            });

            cooldown.play();
        });

        spawnPedestrian.setOnAction(event -> {
            spawnPedestrian();
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
                spawnPedestrian,
                clearAllCars
        );

        controls.setLayoutX(20);
        controls.setLayoutY(WINDOW_HEIGHT - 120);

        createEMSIndicator();

        streetPane.getChildren().add(controls);

    }

    //ems indicator
    private void createEMSIndicator() {

        emsIndicator = new Circle(8, Color.DARKGRAY);

        emsLabel = new Label("EMS SIGNAL: NOT RECEIVED");
        emsLabel.setTextFill(Color.WHITE);

        HBox emsBox = new HBox(8);
        emsBox.getChildren().addAll(emsIndicator, emsLabel);

        emsBox.setLayoutX(20);
        emsBox.setLayoutY(20);

        streetPane.getChildren().add(emsBox);
    }

    //set ems indicator
    private void setEMSIndicator(boolean received) {

        if (received) {

            emsIndicator.setFill(Color.LIMEGREEN);
            emsLabel.setText("EMS SIGNAL: RECEIVED");

            DropShadow glow = new DropShadow();
            glow.setColor(Color.LIMEGREEN);
            glow.setRadius(15);
            glow.setSpread(0.5);

            emsIndicator.setEffect(glow);

        }

        else {

            emsIndicator.setFill(Color.DARKGRAY);
            emsLabel.setText("EMS SIGNAL: NOT RECEIVED");

            emsIndicator.setEffect(null);
        }
    }

    //logic lanes
    private void createLanes() {
        for (LanePosition lane : LanePosition.values()) {
            LaneList.add(new GUILane(lane, Bearing.North));
        }


        for (LanePosition lane : LanePosition.values()) {
            LaneList.add(new GUILane(lane, Bearing.South));
        }

        for (LanePosition lane : LanePosition.values()) {
            LaneList.add(new GUILane(lane, Bearing.East));
        }

        for (LanePosition lane : LanePosition.values()) {
            LaneList.add(new GUILane(lane, Bearing.West));
        }
    }

    //returns the lane
    private GUILane getLane(Bearing bearing, LanePosition lanePosition) {

        int directionIndex;

        switch (bearing) {

            case North:
                directionIndex = 0;
                break;

            case South:
                directionIndex = 1;
                break;

            case East:
                directionIndex = 2;
                break;

            case West:
                directionIndex = 3;
                break;

            default:
                return null;
        }

        //convert position to its index
        int laneIndexWithDirection = lanePosition.ordinal();

        int laneIndex = directionIndex * LANES_PER_DIRECTION + laneIndexWithDirection;

        return LaneList.get(laneIndex);
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
            for(LanePosition lanePosition : LanePosition.values()) {
                drawArrow(bearing, lanePosition);
            }

        }
    }

    //draw arrows
    private void drawArrow(Bearing bearing, LanePosition lanePosition) {
        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2;
        double intersectionRight = intersectionLeft + ROAD_WIDTH;

        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2;
        double intersectionBottom = intersectionTop + ROAD_WIDTH;

        double x = 0;
        double y = 0;

        double rotation = 0;

        int laneNumber = lanePosition.ordinal();

        //Convert the logical lane position to the physical lane on screen
        int physicalLaneNumber = LANES_PER_DIRECTION - 1 - laneNumber;

        Group arrow;

        switch (lanePosition) {

            case Left:
                arrow = createLeftTurnArrow();
                break;

            case Right:
                arrow = createRightTurnArrow();
                break;

            case Middle:
                arrow = createStraightArrow();
                break;

            default:
                return;
        }

        //position and rotate the arrows based on the direction of travel
        switch(bearing) {
            case North:
                x = intersectionLeft + (LANES_PER_DIRECTION + physicalLaneNumber) * LANE_WIDTH + LANE_WIDTH / 2.0;
                y = intersectionBottom + CROSSWALK_WIDTH + CROSSWALK_OFFSET + STOPLINE_WIDTH * 3;

                rotation = 0;
                break;

            case South:
                x = intersectionLeft + (LANES_PER_DIRECTION - 1 - physicalLaneNumber) * LANE_WIDTH + LANE_WIDTH / 2.0;
                y = intersectionTop - CROSSWALK_WIDTH - CROSSWALK_OFFSET - STOPLINE_WIDTH * 3;

                rotation = 180;
                break;

            case East:
                x = intersectionLeft - CROSSWALK_WIDTH - CROSSWALK_OFFSET - STOPLINE_WIDTH * 3;
                y = intersectionTop + (LANES_PER_DIRECTION + physicalLaneNumber) * LANE_WIDTH + LANE_WIDTH / 2.0;

                rotation = 90;
                break;

            case West:
                x = intersectionRight + CROSSWALK_WIDTH + CROSSWALK_OFFSET + STOPLINE_WIDTH * 3;
                y = intersectionTop + (LANES_PER_DIRECTION - 1 - physicalLaneNumber) * LANE_WIDTH +  LANE_WIDTH / 2.0;

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

    private void drawSensors() {

        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2;
        double intersectionRight = (WINDOW_HEIGHT - ROAD_WIDTH) / 2;

        double sensorRadius = 3;

        //distance from the intersection center
        double sensorOffset1 = 120;
        double sensorOffset2 = 200;
        double sensorOffset3 = 280;

        for (int lane = 0; lane < LANES_PER_DIRECTION; lane++) {

            //north
            double northX = intersectionLeft + ROAD_WIDTH / 2
                    + (LANES_PER_DIRECTION - 1 - lane) * LANE_WIDTH
                    + LANE_WIDTH / 2.0;

            drawSensor(northX, intersectionRight + ROAD_WIDTH + sensorOffset1, sensorRadius);
            drawSensor(northX, intersectionRight + ROAD_WIDTH + sensorOffset2, sensorRadius);
            drawSensor(northX, intersectionRight + ROAD_WIDTH + sensorOffset3, sensorRadius);


            //south
            double southX = intersectionLeft - ROAD_WIDTH / 2
                    + (LANES_PER_DIRECTION + lane) * LANE_WIDTH
                    + LANE_WIDTH / 2.0;

            drawSensor(southX, intersectionRight - sensorOffset1, sensorRadius);
            drawSensor(southX, intersectionRight - sensorOffset2, sensorRadius);
            drawSensor(southX, intersectionRight - sensorOffset3, sensorRadius);

            //east
            double eastY = intersectionRight + ROAD_WIDTH / 2
                    + (LANES_PER_DIRECTION - 1 - lane) * LANE_WIDTH
                    + LANE_WIDTH / 2.0;

            drawSensor(intersectionLeft - sensorOffset1, eastY, sensorRadius);
            drawSensor(intersectionLeft - sensorOffset2, eastY, sensorRadius);
            drawSensor(intersectionLeft - sensorOffset3, eastY, sensorRadius);


            //west
            double westY = intersectionRight - ROAD_WIDTH / 2
                    + (LANES_PER_DIRECTION + lane) * LANE_WIDTH
                    + LANE_WIDTH / 2.0;

            drawSensor(intersectionLeft + ROAD_WIDTH + sensorOffset1, westY, sensorRadius);
            drawSensor(intersectionLeft + ROAD_WIDTH + sensorOffset2, westY, sensorRadius);
            drawSensor(intersectionLeft + ROAD_WIDTH + sensorOffset3, westY, sensorRadius);

        }
    }

    private void drawSensor(double x, double y, double radius) {

        Circle sensor = new Circle(x, y, radius, Color.LIMEGREEN);

        //glow
        DropShadow glow = new DropShadow();

        glow.setColor(Color.LIMEGREEN);
        glow.setRadius(10);
        glow.setSpread(0.5);

        sensor.setEffect(glow);

        streetPane.getChildren().add(sensor);
    }

    private void drawPedLight(double x, double y, Bearing bearing){
        double width = 25;
        Rectangle housing;
        Rectangle inner;
        Rectangle button = new Rectangle(width/2, width/2, width/2, width/2);
        Text timer;

        housing = new Rectangle(x , y, width, width);
        housing.setArcWidth(housing.getWidth() * 0.8);
        housing.setArcHeight(housing.getHeight() * 0.8);
        housing.setFill(Color.DARKGRAY);

        inner = new Rectangle(x + width*.15 , y + width*.15, width*.7, width*.7);
        inner.setFill(Color.GRAY);
        inner.setArcHeight(inner.getHeight()*.8);
        inner.setArcWidth(inner.getWidth()*.8);

        timer = new Text(x + width*.25, y + width*.65, "0");
        timer.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 10));

        timer.setTextAlignment(TextAlignment.CENTER);
        timer.setStrokeWidth(1.5);
        timer.setFill(Color.ORANGERED);

        if(bearing == Bearing.North){
            button = new Rectangle(x+ width*.35,y-width*.1,width*.35,width*.1);
            button.setFill(Color.BLACK);
        }
        if(bearing == Bearing.West){
            button = new Rectangle(x- width*.1,y+width*.35,width*.1,width*.35);
            button.setFill(Color.BLACK);
        }
        if(bearing == Bearing.East){
            button = new Rectangle(x + width*.9,y+width*.35,width*.1,width*.35);
            button.setFill(Color.BLACK);
        }
        if(bearing == Bearing.South){
            button = new Rectangle(x+ width*.3,y+width*.9,width*.35,width*.1);
            button.setFill(Color.BLACK);
        }

        streetPane.getChildren().addAll(housing, inner, timer, button);
        pedLights.add(new PedLightVisual(timer));
    }

    //traffic light
    private void drawTrafficLight(double x, double y, Bearing bearing, LightShape shape) {

        //housing
        double housingSize = 30;

        double lightSize = 20;

        //center light inside housing
        double lightX = x - lightSize / 2;
        double lightY = y - lightSize / 2;

        Rectangle housing = new Rectangle(
                x - housingSize / 2,
                y - housingSize / 2, housingSize, housingSize);

        housing.setFill(Color.BLACK);

        housing.setArcWidth(0);
        housing.setArcHeight(0);

        //streetPane.getChildren().add(housing);

        Polygon leftArrow = new Polygon();
        leftArrow.getPoints().addAll(
                x - 8.0, y + 5.0,
                x - 8.0, y - 3.0,
                x - 2.0, y - 3.0,
                x - 2.0, y - 9.0,
                x + 8.0, y,
                x - 2.0, y + 9.0,
                x - 2.0, y + 3.0,
                x - 8.0, y + 3.0
        );

        leftArrow.setFill(Color.RED);

        Polygon rightArrow = new Polygon();
        rightArrow.getPoints().addAll(
                x + 8.0, y + 5.0,
                x + 8.0, y - 3.0,
                x + 2.0, y - 3.0,
                x + 2.0, y - 9.0,
                x - 8.0, y,
                x + 2.0, y + 9.0,
                x + 2.0, y + 3.0,
                x + 8.0, y + 3.0
        );

        rightArrow.setFill(Color.RED);

        Rectangle light = new Rectangle(lightX, lightY, lightSize, lightSize);

        light.setArcWidth(0);
        light.setArcHeight(0);

        //initial color
        light.setFill(Color.RED);

        //rotate arrow for the direction of traffic
        switch (bearing) {

            case North:
                leftArrow.setRotate(180);
                rightArrow.setRotate(180);
                break;

            case South:
                leftArrow.setRotate(0);
                rightArrow.setRotate(0);
                break;

            case East:
                leftArrow.setRotate(270);
                rightArrow.setRotate(270);
                break;

            case West:
                leftArrow.setRotate(90);
                rightArrow.setRotate(90);
                break;
        }

        light.setVisible(false);
        leftArrow.setVisible(false);
        rightArrow.setVisible(false);

        switch(shape) {
            case LeftArrow:
                leftArrow.setVisible(true);
                break;

            case RightArrow:
                rightArrow.setVisible(true);
                break;

            case Square:
                light.setVisible(true);
                break;
        }

        streetPane.getChildren().addAll(housing, light, leftArrow, rightArrow);
        trafficLights.get(bearing).add(new TrafficLightVisual(light, leftArrow, rightArrow));
    }

    //change traffic light colors
    public void changeTrafficLight(int lightID, LightCol color, LightShape shape, Bearing direction) {
        //find light associated with directional group
        ArrayList<TrafficLightVisual> directionalLights = trafficLights.get(direction);

        if (directionalLights == null) {
            System.err.println("ERROR[GUIMain]: No lights found for direction: " + direction);
            return;
        }
        if (lightID < 0 || lightID >= directionalLights.size()) {
            System.err.println("ERROR[GUIMain]: Invalid light ID " + lightID + " for direction " + direction);
            return;
        }
        TrafficLightVisual light = directionalLights.get(lightID);

        //hide every shape
        if(light.getLight() != null) {
            light.getLight().setVisible(false);
        }

        if(light.getLeftTurnArrow() != null) {
            light.getLeftTurnArrow().setVisible(false);
        }

        if(light.getRightTurnArrow() != null) {
            light.getRightTurnArrow().setVisible(false);
        }
        System.out.println("Shape received: " + shape);
        //change traffic lights based on shape
        switch (shape) {
            case LeftArrow:
                if(light.getLeftTurnArrow() != null) {
                    light.getLeftTurnArrow().setVisible(true);

                    switch (color) {

                        case Red:
                            light.getLeftTurnArrow().setFill(Color.RED);
                            break;

                        case Yellow:
                            light.getLeftTurnArrow().setFill(Color.YELLOW);
                            break;

                        case Green:
                            light.getLeftTurnArrow().setFill(Color.GREEN);
                            break;
                    }
                }

                break;

            case RightArrow:
                if(light.getRightTurnArrow() != null) {
                    light.getRightTurnArrow().setVisible(true);

                    switch (color) {

                        case Red:
                            light.getRightTurnArrow().setFill(Color.RED);
                            break;

                        case Yellow:
                            light.getRightTurnArrow().setFill(Color.YELLOW);
                            break;

                        case Green:
                            light.getRightTurnArrow().setFill(Color.GREEN);
                            break;
                    }
                }

                break;

            case Square:
                if(light.getLight() != null) {
                    light.getLight().setVisible(true);
                    switch(color) {
                        case Red:
                            light.getLight().setFill(Color.RED);
                            break;

                        case Yellow:
                            light.getLight().setFill(Color.YELLOW);
                            break;

                        case Green:
                            light.getLight().setFill(Color.GREEN);
                            break;
                    }
                }

                break;
        }


        //update simulation logic
        LanePosition lanePosition;
        switch (direction) {

            case North, East:
                lanePosition = switch (lightID) {
                    case 0 -> LanePosition.Left;
                    case 1 -> LanePosition.Middle;
                    case 2 -> LanePosition.Right;
                    default -> null;
                };
                break;

            case South, West:
                lanePosition = switch (lightID) {
                    case 0 -> LanePosition.Right;
                    case 1 -> LanePosition.Middle;
                    case 2 -> LanePosition.Left;
                    default -> null;
                };
                break;

            default:
                return;
        }

        GUILane lane = getLane(direction, lanePosition);

        assert lane != null;
        lane.updateLights(0, color, shape);



        //update canMove for cars in that lane
        for (CarVisual carVisual : cars) {

            if (carVisual.getCurrentBearing() == direction
                    && carVisual.getLanePosition() == lanePosition) {

                carVisual.getCar().setCanMove(color == LightCol.Green);
            }
        }


        //store logic state
        light.setCurrentColor(color);
    }

    private void createCar(int id, GUILane lane, Bearing bearing, LanePosition lanePosition, boolean EMS) {
        //create the logic car
        GUICar guiCar = new GUICar(id, lane, bearing, lanePosition, null, EMS);
        Image image;

        if(EMS) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/cars/Ambulance.png")));
        }

        else {
            //create visual car
            String imagePath = carImages.get(random.nextInt(carImages.size()));
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        }

        ImageView car = new ImageView(image);

        car.setFitWidth(LANE_WIDTH);
        car.setFitHeight(LANE_WIDTH);

        positionCar(car, bearing, lanePosition);

        streetPane.getChildren().add(car);
        System.out.println("car has been created.");

        //random speed
        double speed = MIN_CAR_SPEED + random.nextDouble() * (MAX_CAR_SPEED - MIN_CAR_SPEED);

        //connect logic car with visual
        CarVisual carVisual = new CarVisual(guiCar, car, speed, lanePosition);

        if (lanePosition != LanePosition.Middle) {
            Bearing pendingBearing = computeTurnBearing(bearing, lanePosition);
            double mergeThreshold = getMergeCoordinate(pendingBearing, lanePosition);
            carVisual.setTurnInfo(pendingBearing, mergeThreshold);
        }

        //store car
        cars.add(carVisual);

        moveCar(carVisual);

        checkEMS();
    }

    //create pedestrians
    private void spawnPedestrian() {
        List<String> selectedPedestrian = pedestrianImages.get(random.nextInt(pedestrianImages.size()));

        ArrayList<Image> walkingFrames = new ArrayList<>();

        for(String imagePath : selectedPedestrian) {
            walkingFrames.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
        }

        ImageView pedestrian = new ImageView(walkingFrames.getFirst());

        pedestrian.setFitWidth(30);
        pedestrian.setFitHeight(30);
        pedestrian.setPreserveRatio(true);

        Bearing bearing = Bearing.values()[random.nextInt(Bearing.values().length)];

        boolean firstCrosswalk = random.nextBoolean(); //randomly chooses what crosswalk to use

        positionPed(pedestrian, bearing, firstCrosswalk);

        double speed = 1.0;

        PedestrianVisual pedestrianVisual =
                new PedestrianVisual(
                        pedestrian,
                        walkingFrames,
                        speed,
                        firstCrosswalk,
                        bearing
        );

        streetPane.getChildren().add(pedestrian);

        System.out.println("Pedestrian has been created.");

        pedestrians.add(pedestrianVisual);

        movePedestrian(pedestrianVisual, bearing);
    }

    //remove car
    private void removeCar(CarVisual carVisual) {
        //stop animation
        if(carVisual.getTimeline() != null) {
            carVisual.getTimeline().stop();
        }

        streetPane.getChildren().remove(carVisual.getImageView());
        cars.remove(carVisual);

        checkEMS();
    }

    private void removePedestrian(PedestrianVisual pedestrianVisual) {
        pedestrianVisual.stopAnimation();

        streetPane.getChildren().remove(pedestrianVisual.getImageView());

        pedestrians.remove(pedestrianVisual);
    }

    //initial position
    //lane number starts at 0, left to right
    private void positionCar(ImageView car, Bearing bearing, LanePosition lanePosition) {
        int laneNumber = lanePosition.ordinal();
        laneNumber = LANES_PER_DIRECTION - 1 - laneNumber;

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

    private Bearing computeTurnBearing(
            Bearing entry,
            LanePosition lanePosition) {

        boolean isLeft = lanePosition == LanePosition.Left;

        return switch (entry) {

            case North -> isLeft ? Bearing.West : Bearing.East;
            case South -> isLeft ? Bearing.East : Bearing.West;
            case East -> isLeft ? Bearing.North : Bearing.South;
            case West -> isLeft ? Bearing.South : Bearing.North;
        };
    }

    private double getRotationForBearing(Bearing bearing) {
        return switch (bearing) {
            case North -> 0;
            case South -> 180;
            case East -> 90;
            case West -> 270;
        };
    }

    // returns the fixed lane coordinate a turning car merges into on its new road
    // (always the "straight" lane, lane index 1, of the destination approach)
    private double getMergeCoordinate(Bearing bearing, LanePosition lanePosition) {
        double roadLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2;
        double roadTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2;

        int laneNumber = lanePosition.ordinal();
        int physicalLane = LANES_PER_DIRECTION - 1 - laneNumber;

        return switch (bearing) {
            case North -> roadLeft + (LANES_PER_DIRECTION + physicalLane) * LANE_WIDTH;
            case South -> roadLeft + (LANES_PER_DIRECTION - 1 - physicalLane) * LANE_WIDTH;
            case East  -> roadTop + (LANES_PER_DIRECTION + physicalLane) * LANE_WIDTH;
            case West  -> roadTop + (LANES_PER_DIRECTION - 1 - physicalLane) * LANE_WIDTH;
        };
    }

    private void positionPed(ImageView pedestrian, Bearing bearing, boolean firstCrosswalk) {
        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2.0;
        double intersectionRight = intersectionLeft + ROAD_WIDTH;
        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2.0;
        double intersectionBottom = intersectionTop + ROAD_WIDTH;

        //starting position
        switch (bearing) {
            case North:
                double northX;

                if(firstCrosswalk) {
                    northX = intersectionLeft - STOPLINE_WIDTH * 2;
                }

                else {
                    northX = intersectionRight + (CROSSWALK_WIDTH / 2) + STOPLINE_WIDTH;
                }

                pedestrian.setX(northX);

                pedestrian.setY(WINDOW_HEIGHT);

                pedestrian.setRotate(0);

                break;

            case South:
                double southX;

                if (firstCrosswalk) {
                    southX = intersectionLeft - CROSSWALK_WIDTH;
                }

                else {
                    southX = intersectionRight;
                }

                pedestrian.setX(southX);

                pedestrian.setY(-LANE_WIDTH);

                pedestrian.setRotate(180);
                break;

            case East:
                double eastY;

                if (firstCrosswalk) {
                    eastY = intersectionTop - STOPLINE_WIDTH * 2;
                }

                else {
                    eastY = intersectionBottom + CROSSWALK_WIDTH - STOPLINE_WIDTH * 2;
                }

                pedestrian.setX(-LANE_WIDTH);

                pedestrian.setY(eastY);

                pedestrian.setRotate(90);

                break;

            case West:
                double westY;

                if (firstCrosswalk) {
                    westY = intersectionTop - CROSSWALK_WIDTH;
                }

                else {
                    westY = intersectionBottom;
                }

                pedestrian.setX(WINDOW_WIDTH);

                pedestrian.setY(westY);

                pedestrian.setRotate(270);
                break;
        }
    }

    private boolean reachedPedStop(PedestrianVisual pedestrian, Bearing bearing) {
        ImageView ped = pedestrian.getImageView();

        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2.0;
        double intersectionRight = intersectionLeft + ROAD_WIDTH;

        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2.0;
        double intersectionBottom = intersectionTop + ROAD_WIDTH;

        double stopDistance = 20;

        return switch (bearing) {
            case North -> ped.getY() <= intersectionBottom + stopDistance;
            case South -> ped.getY() >= intersectionTop - stopDistance * 2;
            case East -> ped.getX() >= intersectionLeft - stopDistance * 2;
            case West -> ped.getX() <= intersectionRight + stopDistance;
        };

    }

    //animation to move the car
    private void moveCar(CarVisual carVisual) {

        ImageView car = carVisual.imageView;

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            Bearing bearing = carVisual.getCurrentBearing();

            //check if this car has reached its turn line (only for left/right lanes)
            if (!carVisual.hasTurned() && carVisual.getLanePosition() != LanePosition.Middle) {
                double threshold = carVisual.getMergeThreshold();
                boolean crossedMerge = switch (bearing) {
                    case North -> car.getY() <= threshold;
                    case South -> car.getY() >= threshold;
                    case East  -> car.getX() >= threshold;
                    case West  -> car.getX() <= threshold;
                };

                if (reachedIntersection(carVisual)) {
                    //unprotected left turn
                    if(carVisual.getLanePosition() == LanePosition.Left
                            && carVisual.getCar().getLane().getLightShape(0) != LightShape.LeftArrow) {

                        //wait for oncoming traffic
                        if(hasOncomingTraffic(carVisual)) {
                            carVisual.setSpeed(0);

                            return;
                        }
                    }

                    startTurn(carVisual);
                    return;
                }
            }

            CarVisual carAhead = getCarAhead(carVisual);

            //stop at the stop line when the car is not allowed to move
            if (!carVisual.getCar().canMove()) {

                if (isAtStopLine(carVisual) && !reachedIntersection(carVisual)) {
                    carVisual.setSpeed(0);
                }

                else {
                    carVisual.setSpeed(carVisual.getOriginalSpeed());
                }
            }

            else {

                carVisual.setSpeed(carVisual.getOriginalSpeed());

            }

            //check the car in front
            if(carAhead != null) {
                //following distance
                if(carAhead.getSpeed() == 0) {
                    carVisual.setSpeed(0);
                }


                else {
                    carVisual.setSpeed(carAhead.getSpeed());
                }
            }

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

            carVisual.getCar().addDistance(speed);

            CarVisual otherCar = isCollidingWithAnotherCar(carVisual);

            if(otherCar != null) {
                System.out.println("Collision!!!");

                carVisual.setSpeed(0);
                otherCar.setSpeed(0);

                carVisual.getTimeline().stop();
                otherCar.getTimeline().stop();
            }

            if(isOutsideScreen(car, carVisual.getCurrentBearing())) {
                removeCar(carVisual);
                System.out.println("car has been removed.");
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        //store timeline for car
        carVisual.setTimeline(timeline);

        timeline.play();
    }

   //turn the car
   private void startTurn(CarVisual carVisual) {
       ImageView car = carVisual.getImageView();

       Bearing startBearing = carVisual.getCurrentBearing();

       boolean leftTurn = carVisual.getLanePosition() == LanePosition.Left;

       Point2D start = new Point2D(car.getX(), car.getY()); //starting point

       Point2D control;
       Point2D end; //ending point

       double turnDistance;

       //left turns use a wider curve
       if (leftTurn) {
           turnDistance = ROAD_WIDTH / 1.5;
       }

       //right turns use a tighter curve
       else {
           turnDistance = ROAD_WIDTH / 6.0;
       }

       double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2.0;

       double intersectionRight = intersectionLeft + ROAD_WIDTH;

       double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2.0;

       double intersectionBottom = intersectionTop + ROAD_WIDTH;


       switch (startBearing) {
           case North:
               control = new Point2D(start.getX(), start.getY() - turnDistance);

               if (leftTurn) {
                   end = new Point2D(intersectionLeft - LANE_WIDTH / 2.0, start.getY() - turnDistance);

               }

               else {
                   end = new Point2D(intersectionRight + LANE_WIDTH / 2.0, start.getY() - turnDistance);
               }

               break;

           case South:

               control = new Point2D(start.getX(), start.getY() + turnDistance);

               if (leftTurn) {
                   end = new Point2D(intersectionRight + LANE_WIDTH / 2.0, start.getY() + turnDistance);

               }

               else {

                   end = new Point2D(intersectionLeft - LANE_WIDTH / 2.0, start.getY() + turnDistance);
               }

               break;

           case East:
               control = new Point2D(start.getX() + turnDistance, start.getY());

               if (leftTurn) {
                   end = new Point2D(start.getX() + turnDistance, intersectionTop - LANE_WIDTH / 2.0);
               }

               else {
                   end = new Point2D(start.getX() + turnDistance, intersectionBottom + LANE_WIDTH / 2.0);
               }

               break;

           case West:

               control = new Point2D(start.getX() - turnDistance, start.getY());

               if (leftTurn) {
                   end = new Point2D(start.getX() - turnDistance, intersectionBottom + LANE_WIDTH / 2.0);
               }

               else {
                   end = new Point2D(start.getX() - turnDistance, intersectionTop - LANE_WIDTH / 2.0);
               }

               break;

           default:
               return;
       }

       //I needed the cars to make smooth turns instead of making
       //a sharp 90 degree turn, so I researched ways to do this and
       //found the Beizer curve, which let me define a starting point,
       //control point, and end point for a car's path.

       moveAlongCurve(carVisual, start, control, end);
   }

   //moves a car smoothly along a quadratic Bezier curve
   private void moveAlongCurve(CarVisual carVisual, Point2D start, Point2D control, Point2D end) {

       ImageView car = carVisual.getImageView();

       //stop current movement
       if (carVisual.getTimeline() != null) {
           carVisual.getTimeline().stop();
       }

       //store the progress through the curve
       //0.0 means the car is at the start
       //1.0 means the car has reached the end
       final double[] progress = {0.0};
       final Timeline[] turnTimeline = new Timeline[1];

       turnTimeline[0] = new Timeline(new KeyFrame(Duration.millis(16), event -> {

           //approximate curve length for converting
           //pixels/frame into curve progress
           double approximateTurnLength;

           //different turn speeds based on if turning left or right
           if(carVisual.getLanePosition() == LanePosition.Left) {
               approximateTurnLength = ROAD_WIDTH / 1.2;
           }

           else {
               approximateTurnLength = ROAD_WIDTH / 4.0;
           }

           double turnSpeed = 3.0;

           progress[0] += turnSpeed / approximateTurnLength;

           //progress cant be more than 1.0
           if (progress[0] >= 1.0) {
               progress[0] = 1.0;
           }

           double t = progress[0];

           /*
            * quadratic Bezier Curve Formula:
            *
            * B(t) = (1-t)^2 * P0 + 2(1-t)t * P1 + t^2 * P2
            *
            * where
            *
            * P0 = start
            * P1 = control
            * P2 = end
            *
            * t = progress from 0.0 to 1.0
            *
            * The formula is calculated separately for x and y
            */

           //x coordinate
           double x = Math.pow(1 - t, 2) * start.getX() + 2 * (1 - t) * t * control.getX() + Math.pow(t, 2) * end.getX();

           //y cooridnate
           double y = Math.pow(1 - t, 2) * start.getY() + 2 * (1 - t) * t * control.getY() + Math.pow(t, 2) * end.getY();

           car.setX(x);
           car.setY(y);

           /*
            * Derivative of a Beizer Curve
            *
            * B'(t) = 2(1-t)(P1-P0) + 2t(P2-P1)
            *
            * This gives us the direction that the car is moving
            * at its current position on the curve
            */

           double dx = 2 * (1 - t) * (control.getX() - start.getX()) + 2 * t * (end.getX() - control.getX());

           double dy = 2 * (1 - t) * (control.getY() - start.getY()) + 2 * t * (end.getY() - control.getY());

           //calculate the angle of the car's current direction
           double angle = Math.toDegrees(Math.atan2(dy, dx));

           //rotate the car so it's facing the direction it is traveling
           car.setRotate(angle + 90);

           //finshed turning
           if (progress[0] >= 1.0) {
               turnTimeline[0].stop();

               carVisual.setCurrentBearing(carVisual.getPendingBearing());
               carVisual.setHasTurned(true);

               //continue driving in the new direction
               moveCar(carVisual);
           }
       }));

       turnTimeline[0].setCycleCount(Timeline.INDEFINITE);

       //store the turning animation
       carVisual.setTimeline(turnTimeline[0]);

       turnTimeline[0].play();
   }



    private void movePedestrian(PedestrianVisual pedestrianVisual, Bearing bearing) {
        ImageView pedestrian = pedestrianVisual.getImageView();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> {

            //don't walk
            if(!pedestrianCanWalk && reachedPedStop(pedestrianVisual, bearing)) {
                return;
            }

            switch(bearing) {
                case North:
                    pedestrian.setY(pedestrian.getY() - pedestrianVisual.getSpeed());
                    break;

                case South:
                    pedestrian.setY(pedestrian.getY() + pedestrianVisual.getSpeed());
                    break;

                case East:
                    pedestrian.setX(pedestrian.getX() + pedestrianVisual.getSpeed());
                    break;

                case West:
                    pedestrian.setX(pedestrian.getX() - pedestrianVisual.getSpeed());
                    break;
            }

            pedestrianVisual.nextFrame(pedestrianVisual.getSpeed());

            boolean leftScreen = switch (bearing) {

                case North -> pedestrian.getY() + pedestrian.getFitHeight() < 0;

                case South -> pedestrian.getY() > WINDOW_HEIGHT;

                case East -> pedestrian.getX() > WINDOW_WIDTH;

                case West -> pedestrian.getX() + pedestrian.getFitWidth() < 0;
            };

            //remove pedestrian after leaving screen
            if (leftScreen) {

                removePedestrian(pedestrianVisual);
                System.out.println("Pedestrian has beed removed.");

            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        pedestrianVisual.setTimeline(timeline);

        timeline.play();
    }

    public void setPedestrianCanWalk(boolean canWalk) {
        pedestrianCanWalk = canWalk;
    }

    //checks if car is too close to the car in front
    private CarVisual getCarAhead(CarVisual carVisual) {


        ImageView car = carVisual.getImageView();


        for (CarVisual otherCar : cars) {


            if (otherCar == carVisual) {
                continue;
            }

            Bearing otherBearing;


            if (otherCar.hasTurned()) {
                otherBearing = otherCar.getCurrentBearing();
            }

            else {
                otherBearing = otherCar.getEntryBearing();
            }


            //must be traveling in the same direction
            if (otherBearing != carVisual.getCurrentBearing()) {
                continue;
            }


            //must be in the same lane
            if(otherCar.getLanePosition() != carVisual.getLanePosition()) {
                continue;
            }


            ImageView other = otherCar.getImageView();


            Bearing bearing = carVisual.getCurrentBearing();

            double followingDistance = 60;

            switch (bearing) {


                case North:


                    if (other.getY() < car.getY() && car.getY() - other.getY() < car.getFitHeight() + followingDistance) {
                        return otherCar;
                    }


                    break;


                case South:


                    if (other.getY() > car.getY() && other.getY() - car.getY() < car.getFitHeight() + followingDistance) {


                        return otherCar;
                    }


                    break;


                case East:
                    if (other.getX() > car.getX() && other.getX() - car.getX() < car.getFitWidth() + followingDistance) {
                        return otherCar;
                    }


                    break;


                case West:
                    if (other.getX() < car.getX() && car.getX() - other.getX() < car.getFitWidth() + followingDistance) {
                            return otherCar;
                    }


                    break;
            }
        }


        return null;
    }

    //true if car has entered intersection
    private boolean reachedIntersection(CarVisual carVisual) {

        ImageView car = carVisual.getImageView();

        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2.0;
        double intersectionRight = intersectionLeft + ROAD_WIDTH;

        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2.0;
        double intersectionBottom = intersectionTop + ROAD_WIDTH;

        return switch (carVisual.getCurrentBearing()) {
            case North -> car.getY() < intersectionBottom;
            case South -> car.getY() > intersectionTop - STOPLINE_WIDTH * 4;
            case East -> car.getX() > intersectionLeft - STOPLINE_WIDTH * 4;
            case West -> car.getX() < intersectionRight;
        };

    }


    // Returns true if the car is at the stop line
    private boolean isAtStopLine(CarVisual carVisual) {

        ImageView car = carVisual.getImageView();

        Bearing bearing = carVisual.getCurrentBearing();

        double intersectionLeft = (WINDOW_WIDTH - ROAD_WIDTH) / 2.0;
        double intersectionRight = intersectionLeft + ROAD_WIDTH;

        double intersectionTop = (WINDOW_HEIGHT - ROAD_WIDTH) / 2.0;
        double intersectionBottom = intersectionTop + ROAD_WIDTH;

        double stopDistance = CROSSWALK_WIDTH + CROSSWALK_OFFSET + STOPLINE_WIDTH * 2;

        return switch (bearing) {
            case North -> car.getY() <= intersectionBottom + stopDistance;
            case South -> car.getY() + car.getFitHeight() >= intersectionTop - stopDistance;
            case East -> car.getX() + car.getFitWidth() >= intersectionLeft - stopDistance;
            case West -> car.getX() <= intersectionRight + stopDistance;
        };

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

        carSpawner = new Timeline(new KeyFrame(Duration.millis(spawnInterval), action -> {
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
        LanePosition lanePosition = LanePosition.values()[random.nextInt(LanePosition.values().length)];

        //check if there is already a car near this spawn point
        for (CarVisual carVisual : cars) {

            //not in the same direction
            if (carVisual.getCurrentBearing() != bearing) {
                continue;
            }

            //doesn't have the same lane position
            if (carVisual.getLanePosition() != lanePosition) {
                continue;
            }

            ImageView otherCar = carVisual.getImageView();

            //too close to spawn spoint
            boolean tooClose = switch (bearing) {
                case North -> otherCar.getY() > WINDOW_HEIGHT - 100;

                case South -> otherCar.getY() < 50;

                case East -> otherCar.getX() < 50;

                case West -> otherCar.getX() > WINDOW_WIDTH - 150;
            };

            if (tooClose) {
                System.out.println("Too Close");
                return; //don't spawn car
            }
        }

        GUILane lane = getLane(bearing, lanePosition);

        createCar(nextCarID, lane, bearing, lanePosition, EMS);

        nextCarID++;
    }

    //check all cars for ems
    private void checkEMS() {

        boolean emsPresent = false;

        for (CarVisual carVisual : cars) {

            if (carVisual.getCar().isEMS()) {
                emsPresent = true;
                break;
            }
        }

        setEMSIndicator(emsPresent);
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

    //check if there is oncoming traffic
    private boolean hasOncomingTraffic(CarVisual turningCar) {

        Bearing bearing = turningCar.getCurrentBearing();

        //only applies to left-turning cars
        if (turningCar.getLanePosition() != LanePosition.Left) {
            return false;
        }

        double centerX = WINDOW_WIDTH / 2.0;
        double centerY = WINDOW_HEIGHT / 2.0;

        for (CarVisual otherCar : cars) {

            //don't check against itself
            if (otherCar == turningCar) {
                continue;
            }

            boolean isOncoming = isOncoming(otherCar, bearing);

            if (!isOncoming) {
                continue;
            }

            //don't block left turn if oncoming traffic is stopped
            if(otherCar.getSpeed() == 0) {
                continue;
            }

            ImageView other = otherCar.getImageView();

            //only check for cars not in a turn lane
            if(otherCar.getLanePosition() != LanePosition.Middle) {
                continue;
            }

            boolean hasPassedCenter = switch (bearing) {
                case North -> other.getY() > centerY;
                case South -> other.getY() < centerY;
                case East -> other.getX() < centerX;
                case West -> other.getX() > centerX;
            };

            //oncoming car has not passed the middle yet
            if (!hasPassedCenter) {
                return true;
            }
        }

        return false;
    }

    private static boolean isOncoming(CarVisual otherCar, Bearing bearing) {
        Bearing otherBearing = otherCar.getCurrentBearing();

        //only check cars coming from the opposite direction
        return (bearing == Bearing.North && otherBearing == Bearing.South)
                || (bearing == Bearing.South && otherBearing == Bearing.North)
                || (bearing == Bearing.East && otherBearing == Bearing.West)
                || (bearing == Bearing.West && otherBearing == Bearing.East);
    }

    //small private helper class to store traffic lights
    private static class TrafficLightVisual {
        private final Rectangle light;
        private final Polygon leftTurnArrow;
        private final Polygon rightTurnArrow;

        private Shape currentShape;
        private LightCol currentColor = LightCol.Red;

        public TrafficLightVisual(Rectangle light, Polygon leftTurnArrow, Polygon rightTurnArrow) {
            this.light = light;
            this.leftTurnArrow = leftTurnArrow;
            this.rightTurnArrow = rightTurnArrow;
        }

        public Polygon getLeftTurnArrow() {
            return leftTurnArrow;
        }

        public Polygon getRightTurnArrow() {
            return rightTurnArrow;
        }

        public Shape getCurrentShape() {
            return currentShape;
        }

        public Rectangle getLight() {
            return light;
        }

        public LightCol getCurrentColor() {
            return currentColor;
        }

        public void setCurrentColor(LightCol currentColor) {
            this.currentColor = currentColor;
        }
    }


    private record PedLightVisual (Text timer) {}

    //small private helper class to store car visuals
    private static class CarVisual {
        private final GUICar car;
        private final ImageView imageView;
        private double speed;
        private final double originalSpeed;
        private Timeline timeline;
        private final LanePosition lanePosition;

        private Bearing entryBearing;

        private boolean hasTurned = false;
        private Bearing pendingBearing;
        private double mergeThreshold;

        private Bearing currentBearing;

        public CarVisual(GUICar car, ImageView imageView, double speed, LanePosition lanePosition) {
            this.car = car;
            this.imageView = imageView;
            this.speed = speed;
            this.originalSpeed = speed;
            this.lanePosition = lanePosition;

            this.currentBearing = car.getBearing();
            this.entryBearing = car.getBearing();
        }

        public Bearing getCurrentBearing() {
            return currentBearing;
        }

        public void setCurrentBearing(Bearing currentBearing) {
            this.currentBearing = currentBearing;
        }

        public Bearing getEntryBearing() {
            return entryBearing;
        }

        public GUICar getCar() {
            return car;
        }

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public double getOriginalSpeed() {
            return originalSpeed;
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

        public LanePosition getLanePosition() {
            return lanePosition;
        }

        public boolean hasTurned() {
            return hasTurned;
        }

        public void setHasTurned(boolean hasTurned) {
            this.hasTurned = hasTurned;
        }

        public Bearing getPendingBearing() {
            return pendingBearing;
        }

        public double getMergeThreshold() {
            return mergeThreshold;
        }

        public void setTurnInfo(Bearing pendingBearing, double mergeThreshold) {
            this.pendingBearing = pendingBearing;
            this.mergeThreshold = mergeThreshold;
        }
    }

    //pedestrian visuals
    private static class PedestrianVisual {
        private final ImageView imageView;
        private final ArrayList<Image> walkingFrames; //walking animation

        private final double speed;

        private final boolean firstCrosswalk;

        private double distanceSinceLastFrame = 0;

        private int currentFrame = 0;

        private Timeline timeline;

        public PedestrianVisual(ImageView imageView, ArrayList<Image> walkingFrames, double speed, boolean firstCrosswalk, Bearing bearing) {
            this.imageView = imageView;
            this.walkingFrames = walkingFrames;
            this.speed = speed;
            this.firstCrosswalk = firstCrosswalk;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public double getSpeed() {
            return speed;
        }

        public boolean isFirstCrosswalk() {
            return firstCrosswalk;
        }

        public int getCurrentFrame() {
            return currentFrame;
        }

        //next walking frame
        public void nextFrame(double distanceMoved) {
            distanceSinceLastFrame += distanceMoved;

            //change animation frame every 8 pixels traveled
            double frameDistance = 8.0;

            if(distanceSinceLastFrame >= frameDistance) {
                distanceSinceLastFrame -= frameDistance;

                currentFrame++;

                if(currentFrame >= walkingFrames.size()) {
                    currentFrame = 0;
                }

                //change image
                imageView.setImage(walkingFrames.get(currentFrame));
            }

        }

        public Timeline getTimeline() {
            return timeline;
        }

        public void setTimeline(Timeline timeline) {
            this.timeline = timeline;
        }

        public void stopAnimation() {
            if(timeline != null) {
                timeline.stop();
            }
        }
    }
}
