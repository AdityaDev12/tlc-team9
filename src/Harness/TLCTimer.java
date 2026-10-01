package Harness;

import java.util.function.BooleanSupplier;

public class TLCTimer {
    public void waitFor(long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }
    public boolean waitFor(long milliseconds, BooleanSupplier shouldPreempt) throws InterruptedException {
        long elapsed = 0;
        long checkInterval = 100;
        while (elapsed < milliseconds) {
            //check if EMS has been requested
            if (shouldPreempt.getAsBoolean()) {
                return false;
            }
            long remaining = milliseconds - elapsed;
            long waitTime = Math.min(checkInterval, remaining);
            Thread.sleep(waitTime);
            elapsed += waitTime;
        }
        // check again after final sleep
        return !shouldPreempt.getAsBoolean();
    }

}
