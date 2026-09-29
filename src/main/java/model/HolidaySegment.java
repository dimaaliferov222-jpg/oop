package model;

import java.time.Duration;

public record HolidaySegment(Duration duration) implements TimeSegment {
    public long calculatePay(long rate) {
        return TimeType.HOLIDAY.calculatePay(duration, rate);
    }

    public TimeType type() {
        return TimeType.HOLIDAY;
    }
}
