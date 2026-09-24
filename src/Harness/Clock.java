package Harness;

import java.time.LocalTime;

public class Clock {
    private static final LocalTime DAY_START = LocalTime.of(6,0);
    private static final LocalTime DAY_END =  LocalTime.of(18,0);

    // true if curr time is between 6AM and 6PM
    public boolean isDayTime() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(DAY_START) && now.isBefore(DAY_END);
    }
}
