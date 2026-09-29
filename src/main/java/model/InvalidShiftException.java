package model;

/**
 * Собственное проверяемое исключение для невалидных данных смены.
 */
public class InvalidShiftException extends Exception {
    public InvalidShiftException(String message) {
        super(message);
    }
}