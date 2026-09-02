package Communication;

/**
 * A stand-in NetworkChannel for local testing, before the real Harness
 * Socket component is ready. sendLine() just records what was sent instead
 * of putting it on a real socket; you can call simulateIncoming(...) to
 * pretend a line arrived from the server and exercise InstructionReceiver.
 *
 * Delete or ignore this once the real socket class exists, nothing else
 * in this package depends on it.
 */
/*
public class FakeNetworkChannel implements NetworkChannel {

    private final List<String> sentLines = new ArrayList<>();
    private LineListener listener;

    @Override
    public void sendLine(String line) {
        sentLines.add(line);
        System.out.println("[FakeNetworkChannel] sent: " + line);
    }

    @Override
    public void setLineListener(LineListener listener) {
        this.listener = listener;
    }

    // Test helper: pretend this line just arrived from the server.
    public void simulateIncoming(String rawLine) {
        if (listener != null) {
            listener.onLine(rawLine);
        }
    }

    public List<String> getSentLines() {
        return sentLines;
    }
}
*/
