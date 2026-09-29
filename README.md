# Табель учёта рабочего времени

## Описание модели
Система учёта рабочего времени сотрудников.

### Основные сущности:
- `Employee` — сотрудник с идентификатором и ставкой
- `Shift` — смена с началом и концом
- `MonthlyTimesheet` — табель за месяц
- `TimeSegment` (sealed interface) — отрезок времени
- `TimeType` (enum) — тип времени с коэффициентом

## Особенности
- Java 21, record, sealed interface
- Деньги в копейках (long)
- Собственное проверяемое исключение

## Запуск
mvn clean compile exec:java -Dexec.mainClass="demo.Main"

## Тесты
mvn test