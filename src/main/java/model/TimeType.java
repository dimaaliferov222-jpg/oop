package model;

import java.time.Duration;

/**
 * Тип времени с коэффициентом оплаты.
 */
public enum TimeType {
    REGULAR(1.0),
    NIGHT(1.4),
    OVERTIME(1.5),
    HOLIDAY(2.0);

    private final double coefficient;

    TimeType(double coefficient) {
        this.coefficient = coefficient;
    }

    public double getCoefficient() {
        return coefficient;
    }

    /**
     * Вычисление оплаты в копейках за отрезок времени.
     */
    public long calculatePay(Duration duration, long baseRateKopecksPerHour) {
        if (baseRateKopecksPerHour < 0) {
            throw new IllegalArgumentException("Ставка не может быть отрицательной");
        }
        double hours = duration.toMinutes() / 60.0;

        return Math.round(hours * baseRateKopecksPerHour * coefficient);
    }
}