package Protocol;


public interface NetworkChannel {

    /** Sends one already-formatted wire-format line out over the socket. */
    void sendLine(String line);

    /** Registers the callback invoked once per line received from the socket. */
    void setLineListener(LineListener listener);

    @FunctionalInterface
    interface LineListener {
        void onLine(String rawLine);
    }
}
