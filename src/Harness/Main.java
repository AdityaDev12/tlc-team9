package Harness;
import Simulator.GUIMain;
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
            Mux mux = new Mux();
        } catch (IOException e) {
            System.err.println("ERROR: Main");
            e.printStackTrace();
        }
    }
}