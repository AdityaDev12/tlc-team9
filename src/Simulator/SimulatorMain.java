package Simulator;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Starts GUIMain, starts SimulatorServer
 */

public class SimulatorMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Starting TLC Simulator...");

        // Creates and displays simulator GUI
        GUIMain simulator = new GUIMain(primaryStage);
        simulator.makeGUI();
        // Creates socket server
        SimulatorServer server = new SimulatorServer(simulator);
        // Lets GUIMain send sensor/button/EMS events back out through the server
        simulator.setServer(server);
        // Runs server on separate thread (start GUI without waiting for harness connect)
        Thread serverThread = new Thread(() ->
                server.start(), "SimulatorServerThread"
        );
        serverThread.start();
    }
}