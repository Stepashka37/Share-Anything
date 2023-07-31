package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse anyOtherExc(final Exception exc) {
        log.error("500: " + exc.getMessage());
        return new ErrorResponse("Internal server error", exc.getMessage());
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse annotationValidationExc(final RuntimeException exc) {
        log.error("400: " + exc.getMessage());
        return new ErrorResponse("Ошибка валидации данных", exc.getMessage());
    }

    @ExceptionHandler(StateValidationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse unsupportedStatusExc(final StateValidationException exc) {
        log.error("500: " + exc.getMessage());
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", exc.getMessage());
    }




}
