package demo;

import model.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Main {
    public static void main(String[] args) {
        var zone = ZoneId.of("Europe/Moscow");

        var emp1 = new Employee(new EmployeeId("101"), "Петров П.П.", 30000);
        var emp2 = new Employee(new EmployeeId("102"), "Сидоров С.С.", 45000);
        var timesheet1 = new MonthlyTimesheet(emp1, 2026, 9);
        var timesheet2 = new MonthlyTimesheet(emp2, 2026, 9);

        System.out.println("--- Наполнение табелей (демонстрация >30 объектов) ---");

        for (int i = 1; i <= 30; i++) {
            try {
                var start = ZonedDateTime.of(2026, 9, i, 9, 0, 0, 0, zone);
                var end = start.plusHours(8);
                timesheet1.addShift(new Shift(start, end));
            } catch (InvalidShiftException e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }

        try {
            for (int i = 1; i <= 15; i++) {
                // Ночные смены с 22:00 до 06:00
                var start = ZonedDateTime.of(2026, 9, i, 22, 0, 0, 0, zone);
                var end = start.plusHours(8);
                timesheet2.addShift(new Shift(start, end));
            }

            var overtimeStart = ZonedDateTime.of(2026, 9, 16, 9, 0, 0, 0, zone);
            timesheet2.addShift(new Shift(overtimeStart, overtimeStart.plusHours(12)));
        } catch (InvalidShiftException e) {
            System.err.println("Ошибка добавления: " + e.getMessage());
        }

        System.out.println("\n--- Демонстрация обработки исключений ---");
        try {
            timesheet1.addShift(new Shift(
                    ZonedDateTime.of(2026, 10, 1, 9, 0, 0, 0, zone),
                    ZonedDateTime.of(2026, 10, 1, 17, 0, 0, 0, zone)
            ));
        } catch (InvalidShiftException e) {
            System.out.println("Перехвачено исключение: " + e.getMessage());
        }

        System.out.println("\n" + timesheet1.generateReport());
        System.out.println(timesheet2.generateReport());

        System.out.println("--- Демонстрация инкапсуляции ---");
        var shifts = timesheet1.getShifts();
        try {
            shifts.clear();
        } catch (UnsupportedOperationException e) {
            System.out.println("Попытка изменить внешний список заблокирована (UnsupportedOperationException). Инкапсуляция работает!");
        }
    }
}