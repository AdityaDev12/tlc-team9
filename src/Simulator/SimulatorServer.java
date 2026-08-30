package Simulator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  Opens port, waits for mux in harness
 *
 *  NEEDS: message handling, light commands, streams...
 */

public class SimulatorServer {
    private static final int PORT = 5001;

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("Server started.");
            System.out.println("Waiting for harness...");

            Socket socket = serverSocket.accept();
            System.out.println("Server connected.");
        } catch (IOException e) {
            System.err.println("ERROR: SimulatorServer");
            e.printStackTrace();
        }
    }
}
