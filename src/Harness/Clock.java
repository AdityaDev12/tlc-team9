package Harness;

import java.time.LocalTime;

public class Clock {
    private static final LocalTime DAY_START = LocalTime.of(6,0);
    private static final LocalTime DAY_END =  LocalTime.of(18,0);

    // CHANGE BOOLEAN TO TRUE TO FORCE NIGHTMODE
    private static final boolean FORCE_NIGHT = false;

    // true if curr time is between 6AM and 6PM
    public boolean isDayTime() {
        // uses NightMode if FORCE_NIGHT = true
        if (FORCE_NIGHT) {
            return false;
        }
        LocalTime now = LocalTime.now();
        return !now.isBefore(DAY_START) && now.isBefore(DAY_END);
    }
}
