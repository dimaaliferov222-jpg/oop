package model;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Record для смены. Валидация происходит в компактном конструкторе.
 */
public record Shift(ZonedDateTime start, ZonedDateTime end) {

    public Shift {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Время начала и конца не может быть null");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Конец смены должен быть позже начала");
        }
    }

    public Duration duration() {
        return Duration.between(start, end);
    }
}