package ru.practicum.shareit.exception;

public class StateValidationException extends RuntimeException {
    public StateValidationException(String message) {
        super(message);
    }
}
