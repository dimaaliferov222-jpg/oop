package model;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Табель учёта рабочего времени за месяц.
 */
public class MonthlyTimesheet {
    private final Employee employee;
    private final int year;
    private final int month;

    private final List<Shift> shifts = new ArrayList<>();

    public MonthlyTimesheet(Employee employee, int year, int month) {
        this.employee = employee;
        this.year = year;
        this.month = month;
    }

    /**
     * Добавление смены. Может выбросить проверяемое исключение.
     */
    public void addShift(Shift shift) throws InvalidShiftException {

        if (shift.start().getYear() != year || shift.start().getMonthValue() != month) {
            throw new InvalidShiftException("Смена не принадлежит месяцу табеля: " + shift.start());
        }
        shifts.add(shift);
    }

    /**
     * Инкапсуляция: возвращаем неизменяемую копию списка.
     */
    public List<Shift> getShifts() {
        return List.copyOf(shifts);
    }

    /**
     * Суммарное отработанное время.
     */
    public Duration getTotalDuration() {
        return shifts.stream()
                .map(Shift::duration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Разбиение смен на отрезки разных типов (акцент варианта).
     * Алгоритм объединяет соседние часы одного типа в один сегмент.
     */
    public List<TimeSegment> splitShiftsIntoSegments() {
        var segments = new ArrayList<TimeSegment>();

        for (var shift : shifts) {
            var current = shift.start();
            var end = shift.end();

            while (current.isBefore(end)) {
                var nextHour = current.plusHours(1).truncatedTo(java.time.temporal.ChronoUnit.HOURS);
                if (nextHour.isAfter(end)) {
                    nextHour = end;
                }

                var duration = Duration.between(current, nextHour);
                int hour = current.getHour();

                TimeType currentType = (hour >= 22 || hour < 6) ? TimeType.NIGHT : TimeType.REGULAR;

                if (!segments.isEmpty()) {
                    var lastSegment = segments.getLast();
                    if (lastSegment.type() == currentType) {

                        var mergedDuration = lastSegment.duration().plus(duration);
                        segments.removeLast();

                        TimeSegment mergedSegment = switch (currentType) {
                            case NIGHT -> new NightSegment(mergedDuration);
                            case REGULAR -> new RegularSegment(mergedDuration);
                            default -> throw new IllegalStateException("Неожиданный тип: " + currentType);
                        };
                        segments.add(mergedSegment);
                        current = nextHour;
                        continue;
                    }
                }

                TimeSegment segment = switch (currentType) {
                    case NIGHT -> new NightSegment(duration);
                    case REGULAR -> new RegularSegment(duration);
                    default -> throw new IllegalStateException("Неожиданный тип: " + currentType);
                };
                segments.add(segment);
                current = nextHour;
            }
        }
        return segments;
    }

    /**
     * Отчёт по месяцу с итогом по каждому типу (используем switch с pattern matching).
     */
    public String generateReport() {
        var segments = splitShiftsIntoSegments();
        var report = new StringBuilder();
        report.append(String.format("Табель сотрудника %s за %d-%02d\n", employee.getName(), year, month));

        long totalPay = 0;
        var grouped = segments.stream().collect(Collectors.groupingBy(TimeSegment::type));

        for (var entry : grouped.entrySet()) {
            var type = entry.getKey();
            var totalDuration = entry.getValue().stream()
                    .map(TimeSegment::duration)
                    .reduce(Duration.ZERO, Duration::plus);

            long pay = type.calculatePay(totalDuration, employee.getBaseRateKopecksPerHour());
            totalPay += pay;

            var typeDescription = switch (type) {
                case REGULAR -> "Обычное время";
                case NIGHT -> "Ночное время";
                case OVERTIME -> "Сверхурочное";
                case HOLIDAY -> "Праздничное";
            };

            report.append(String.format("%-15s | %s | Оплата: %d руб. %02d коп.\n",
                    typeDescription, formatDuration(totalDuration), pay / 100, pay % 100));
        }

        report.append(String.format("ИТОГО К ВЫПЛАТЕ: %d руб. %02d коп.\n", totalPay / 100, totalPay % 100));
        return report.toString();
    }

    private String formatDuration(Duration duration) {
        return String.format("%d ч. %d мин.", duration.toHours(), duration.toMinutesPart());
    }
}