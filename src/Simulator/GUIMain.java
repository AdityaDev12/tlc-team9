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

public class GUIHub{
    private Stage primaryStage;

    public GUIHub(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void makeGUI() {
        primaryStage.setTitle("TLC Team9");

    }

    public void changeLight(int ID, LightCol Color){

    }
}