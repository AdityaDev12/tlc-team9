package Harness;
import Communication.InstructionMessage;
import Communication.SimulatorEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;

public class Mux {
    private static final String HOST = "localhost";
    private static final int PORT = 5001;

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public Mux() throws IOException {
        System.out.println("Harness connecting to Simulator...");
        socket = new Socket(HOST, PORT);
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Harness connected.");
    }

    public void sendInstruction(InstructionMessage message) {
        output.println(message.toWireFormat());
        System.out.println("Sent instruction: " + message.toWireFormat());
    }

    public SimulatorEvent receiveEvent() throws IOException {
        String rawMessage = input.readLine();
        if (rawMessage == null) return null;
        return SimulatorEvent.parse(rawMessage);
    }

    /** Background thread: continuously prints incoming sensor/button/EMS events as they arrive. */
    public void listenForEvents() {
        Thread reader = new Thread(() -> {
            try {
                while (true) {
                    SimulatorEvent event = receiveEvent();
                    if (event == null) {
                        System.out.println("[Harness] Simulator closed the connection.");
                        break;
                    }
                    System.out.println("[Simulator -> Harness] " + event.getCommand()
                            + " target=" + event.getTarget() + " value=" + event.getValue());
                }
            } catch (IOException e) {
                System.out.println("[Harness] connection lost: " + e.getMessage());
            }
        }, "Mux-event-listener");
        reader.setDaemon(true);
        reader.start();
    }

    public void close() throws IOException {
        socket.close();
        System.out.println("Harness connection closed.");
    }
}