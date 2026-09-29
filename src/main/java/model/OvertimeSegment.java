package model;

import java.time.Duration;

public record OvertimeSegment(Duration duration) implements TimeSegment {
    public long calculatePay(long rate) {
        return TimeType.OVERTIME.calculatePay(duration, rate);
    }

    public TimeType type() {
        return TimeType.OVERTIME;
    }
}
