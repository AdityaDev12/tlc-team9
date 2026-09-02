package Harness;
import Communication.InstructionMessage;
import Communication.SimulatorEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
/**
 *  Connect to Simulator, PrintWriter sends commands, BufferedReader receives info
 */
public class Mux {
    private static final String HOST = "localhost";
    private static final int PORT = 5001;

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    // Connects to Simulator
    public Mux() throws IOException {
        System.out.println("Harness connecting to Simulator...");
        socket = new Socket (HOST, PORT);
        output = new PrintWriter(socket.getOutputStream(),true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Harness connected.");
    }

    // Sends instruction from Harness to Simulator
    public void sendInstruction(InstructionMessage message) {
        output.println(message.toWireFormat());
        System.out.println("Sent instruction: " + message.toWireFormat());
    }

    // Receives event from Simulator
    public SimulatorEvent receiveEvent() throws IOException {
        String rawMessage = input.readLine(); // WAITING
        if (rawMessage == null) {
            return null;
        }
        System.out.println("Received event: " + rawMessage);
        return SimulatorEvent.parse(rawMessage);
    }

    // Closes connection to Simulator
    public void close() throws IOException {
        socket.close();
        System.out.println("Harness connection closed.");
    }
}
