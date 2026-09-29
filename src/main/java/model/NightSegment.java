package model;

import java.time.Duration;

public record NightSegment(Duration duration) implements TimeSegment {
    public long calculatePay(long rate) {
        return TimeType.NIGHT.calculatePay(duration, rate);
    }

    public TimeType type() {
        return TimeType.NIGHT;
    }
}
