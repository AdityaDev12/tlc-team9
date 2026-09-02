package Simulator;

import Communication.InstructionMessage;
import Communication.SimulatorEvent;
import Communication.TLCCommand;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  Opens port, waits for mux in harness
 *  receives instructions, sends events
 *  handles instruction
 */

public class SimulatorServer {
    private static final int PORT = 5001;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    // Server's access to GUIMain
    private final GUIMain gui;
    public SimulatorServer(GUIMain gui) {
        this.gui = gui;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("Server started.");
            System.out.println("Waiting for harness...");

            socket = serverSocket.accept();
            System.out.println("Server connected.");

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(),true);

            // Continuously listens for instruction from Harness
            while (true) {
                InstructionMessage message = receiveInstruction();
                if (message == null) {
                    break;
                }
                handleInstruction(message);
            }

        } catch (IOException e) {
            System.err.println("ERROR: SimulatorServer");
            e.printStackTrace();
        }
    }

    // Receives instruction from Harness
    public InstructionMessage receiveInstruction() throws IOException {
        String rawMessage = input.readLine();
        if (rawMessage == null) {
            return null;
        }
        System.out.println("Received instruction: " + rawMessage);
        return InstructionMessage.parse(rawMessage);
    }

    // Handles instruction from Harness
    private void handleInstruction(InstructionMessage message) {
        System.out.println("Handling instruction: " + message.toWireFormat());
        // SET_LIGHT_STATE
        if (message.getCommand() == TLCCommand.SET_LIGHT_STATE) {
            Bearing guiBearing = directionToBearing(message.getDirection());
            Platform.runLater(() -> {
                gui.changeTrafficLight(message.getLightID(), message.getColor(), message.getShape(), guiBearing);
            });
        }
    }

    // Sends event to Harness
    public void sendEvent(SimulatorEvent event) {
        output.println(event.toWireFormat());
        System.out.println("Sent event: " + event.toWireFormat());
    }

    // Closes simulator socket connection
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
        System.out.println("Simulator connection closed.");
    }

    // Converts bearing into physical position
    private Bearing directionToBearing(Position position) {
        return switch (position) {
            case North -> Bearing.South;
            case South -> Bearing.North;
            case East -> Bearing.West;
            case West -> Bearing.East;
        };
    }
}
