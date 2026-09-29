package model;

import java.time.Duration;

public record RegularSegment(Duration duration) implements TimeSegment {
    public long calculatePay(long rate) {
        return TimeType.REGULAR.calculatePay(duration, rate);
    }

    public TimeType type() {
        return TimeType.REGULAR;
    }
}
