package model;

import java.time.Duration;

/**
 * Закрытая иерархия отрезков времени.
 */
public sealed interface TimeSegment permits RegularSegment, NightSegment, OvertimeSegment, HolidaySegment {
    Duration duration();
    long calculatePay(long baseRateKopecksPerHour);
    TimeType type();
}

