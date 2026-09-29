package model;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TimesheetTest {

    private final ZoneId ZONE = ZoneId.of("Europe/Moscow");
    private final EmployeeId ID1 = new EmployeeId("001");
    private final Employee EMPLOYEE = new Employee(ID1, "Иванов И.И.", 50000); // 500 руб/час

    // --- 1-3. Контракт equals/hashCode для record ---
    @Test
    void testEmployeeIdEquals() {
        var id2 = new EmployeeId("001");
        assertEquals(ID1, id2);
    }

    @Test
    void testEmployeeIdNotEquals() {
        var id2 = new EmployeeId("002");
        assertNotEquals(ID1, id2);
    }

    @Test
    void testEmployeeIdHashCode() {
        var id2 = new EmployeeId("001");
        assertEquals(ID1.hashCode(), id2.hashCode());
    }

    // --- 4-6. Проверка выброса исключений (Shift) ---
    @Test
    void testValidShift() {
        var start = ZonedDateTime.of(2026, 9, 1, 9, 0, 0, 0, ZONE);
        var end = start.plusHours(8);
        assertDoesNotThrow(() -> new Shift(start, end));
    }

    @Test
    void testInvalidShiftStartEqualsEnd() {
        var time = ZonedDateTime.of(2026, 9, 1, 9, 0, 0, 0, ZONE);
        assertThrows(IllegalArgumentException.class, () -> new Shift(time, time));
    }

    @Test
    void testInvalidShiftStartAfterEnd() {
        var start = ZonedDateTime.of(2026, 9, 1, 18, 0, 0, 0, ZONE);
        var end = start.minusHours(2);
        assertThrows(IllegalArgumentException.class, () -> new Shift(start, end));
    }

    // --- 7-8. Перечисление и коэффициенты ---
    @Test
    void testTimeTypeRegularPay() {
        var pay = TimeType.REGULAR.calculatePay(Duration.ofHours(1), 10000); // 100 руб
        assertEquals(10000, pay);
    }

    @Test
    void testTimeTypeNightPay() {
        // 100 руб * 1.4 = 140 руб = 14000 коп
        var pay = TimeType.NIGHT.calculatePay(Duration.ofHours(1), 10000);
        assertEquals(14000, pay);
    }

    // --- 9-12. Табель, границы и разбиение ---
    @Test
    void testTimesheetAddShiftAndDuration() throws InvalidShiftException {
        var ts = new MonthlyTimesheet(EMPLOYEE, 2026, 9);
        var shift = new Shift(
                ZonedDateTime.of(2026, 9, 1, 9, 0, 0, 0, ZONE),
                ZonedDateTime.of(2026, 9, 1, 17, 0, 0, 0, ZONE)
        );
        ts.addShift(shift);
        assertEquals(Duration.ofHours(8), ts.getTotalDuration());
    }

    @Test
    void testTimesheetBoundaryEmpty() {
        var ts = new MonthlyTimesheet(EMPLOYEE, 2026, 9);
        assertEquals(Duration.ZERO, ts.getTotalDuration());
        assertTrue(ts.getShifts().isEmpty());
    }

    @Test
    void testTimesheetInvalidMonth() {
        var ts = new MonthlyTimesheet(EMPLOYEE, 2026, 9);
        var shift = new Shift(
                ZonedDateTime.of(2026, 10, 1, 9, 0, 0, 0, ZONE), // Октябрь!
                ZonedDateTime.of(2026, 10, 1, 17, 0, 0, 0, ZONE)
        );
        // Проверяем выброс ПРОВЕРЯЕМОГО исключения
        assertThrows(InvalidShiftException.class, () -> ts.addShift(shift));
    }

    @Test
    void testShiftSplittingNightAndRegular() throws InvalidShiftException {
        var ts = new MonthlyTimesheet(EMPLOYEE, 2026, 9);
        // Смена с 20:00 до 04:00 (пересекает ночь)
        var shift = new Shift(
                ZonedDateTime.of(2026, 9, 1, 20, 0, 0, 0, ZONE),
                ZonedDateTime.of(2026, 9, 2, 4, 0, 0, 0, ZONE)
        );
        ts.addShift(shift);

        var segments = ts.splitShiftsIntoSegments();
        // Ожидаем: 2 часа обычных (20-22), 6 часов ночных (22-04)
        assertEquals(2, segments.size());
        assertEquals(TimeType.REGULAR, segments.get(0).type());
        assertEquals(Duration.ofHours(2), segments.get(0).duration());
        assertEquals(TimeType.NIGHT, segments.get(1).type());
        assertEquals(Duration.ofHours(6), segments.get(1).duration());
    }
}