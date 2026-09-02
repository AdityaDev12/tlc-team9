package Harness;
import Communication.InstructionMessage;
import Communication.SimulatorEvent;
import Communication.TLCCommand;
import Simulator.*;

import java.io.IOException;

/**
 * Temporary test, creates mux
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Harness...");
        try {
            Mux mux = new Mux(); // connects to simulator

            // Test message
            InstructionMessage message = new InstructionMessage(
                    TLCCommand.SET_LIGHT_STATE,
                    0,
                    LightCol.Green,
                    LightShape.Circle,
                    Position.East
            );
            mux.sendInstruction(message);

            // Receive event from Simulator
            SimulatorEvent event = mux.receiveEvent();
            if (event != null) {
                System.out.println("Event Command: " + event.getCommand());
                System.out.println("Event Target: " + event.getTarget());
                System.out.println("Event Value: " + event.getValue());
            }

        } catch (IOException e) {
            System.err.println("ERROR: Main");
            e.printStackTrace();
        }
    }
}