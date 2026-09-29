package model;

/**
 * Record для идентификатора сотрудника.
 * Автоматически реализует корректный equals/hashCode.
 */
public record EmployeeId(String tabNumber) {
    public EmployeeId {
        if (tabNumber == null || tabNumber.isBlank()) {
            throw new IllegalArgumentException("Табельный номер не может быть пустым");
        }
    }
}