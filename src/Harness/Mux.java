package Harness;
import java.io.IOException;
import java.net.Socket;
/**
 *  Connect to Simulator
 *  NEED: send commands, receive info
 */
public class Mux {
    private static final String HOST = "localhost";
    private static final int PORT = 5001;

    private Socket socket;

    public Mux() throws IOException {
        System.out.println("Harness connecting to Simulator...");
        socket = new Socket (HOST, PORT);
        System.out.println("Harness connected.");
    }
}
