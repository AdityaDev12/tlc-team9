package Harness;
import Communication.InstructionMessage;
import Communication.SimulatorEvent;
import Communication.TLCCommand;
import Simulator.Bearing;
import Simulator.GUIMain;
import Simulator.LightCol;
import Simulator.LightShape;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Temporary test, creates mux
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Harness...");
        try {
            Mux mux = new Mux(); // connects to simulator

        } catch (IOException e) {
            System.err.println("ERROR: Main");
            e.printStackTrace();
        }
    }
}