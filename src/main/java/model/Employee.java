package model;

/**
 * Класс сотрудника. Состояние инкапсулировано.
 */
public class Employee {
    private final EmployeeId id;
    private final String name;
    private final long baseRateKopecksPerHour;

    public Employee(EmployeeId id, String name, long baseRateKopecksPerHour) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Имя не может быть пустым");
        if (baseRateKopecksPerHour < 0) throw new IllegalArgumentException("Ставка не может быть отрицательной");

        this.id = id;
        this.name = name;
        this.baseRateKopecksPerHour = baseRateKopecksPerHour;
    }

    public EmployeeId getId() { return id; }
    public String getName() { return name; }
    public long getBaseRateKopecksPerHour() { return baseRateKopecksPerHour; }
}