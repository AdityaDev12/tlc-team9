package Simulator;

import Communication.InstructionMessage;
import Communication.SimulatorEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  Opens port, waits for mux in harness
 *  receives instructions, sends events
 */

public class SimulatorServer {
    private static final int PORT = 5001;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("Server started.");
            System.out.println("Waiting for harness...");

            Socket socket = serverSocket.accept();
            System.out.println("Server connected.");

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(),true);

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
}
