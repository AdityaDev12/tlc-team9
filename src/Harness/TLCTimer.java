package Harness;

public class TLCTimer {
    public void waitFor(long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }
    public void schedule(Runnable task, long millisseconds) {
        Thread timerThread = new Thread(() -> {
            try {
                Thread.sleep(millisseconds);
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "TLC-Timer");
        timerThread.setDaemon(true);
        timerThread.start();
    }
}
