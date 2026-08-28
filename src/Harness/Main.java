package Harness;

import Simulator.GUIMain;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("Starting TLC Team9");

        GUIMain simulator = new GUIMain(primaryStage);
        simulator.makeGUI();

    }
}