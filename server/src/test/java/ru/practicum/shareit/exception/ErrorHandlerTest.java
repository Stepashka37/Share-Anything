package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void itShouldHandleUserNotFoundExc() {
        // Given
        UserNotFoundException userNotFoundException = new UserNotFoundException("User not found");
        ErrorResponse result = errorHandler.notFoundExc(userNotFoundException);
        // When
        // Then
        assertThat(result).isNotNull();
        assertThat(userNotFoundException.getMessage()).isEqualTo(result.getDescription());
    }

    @Test
    void itShouldHandleItemNotFoundExc() {
        // Given
        ItemNotFoundException itemNotFoundException = new ItemNotFoundException("Item not found");
        ErrorResponse result = errorHandler.notFoundExc(itemNotFoundException);
        // When
        // Then
        assertThat(result).isNotNull();
        assertThat(itemNotFoundException.getMessage()).isEqualTo(result.getDescription());
    }

    @Test
    void itShouldHandleBookingNotFoundExc() {
        // Given
        BookingNotFoundException bookingNotFoundException = new BookingNotFoundException("Booking not found");
        ErrorResponse result = errorHandler.notFoundExc(bookingNotFoundException);
        // When
        // Then
        assertThat(result).isNotNull();
        assertThat(bookingNotFoundException.getMessage()).isEqualTo(result.getDescription());
    }

    @Test
    void itShouldHandleHostNotFoundExc() {
        // Given
        HostNotFoundException hostNotFoundException = new HostNotFoundException("Host not found");
        ErrorResponse result = errorHandler.notFoundExc(hostNotFoundException);
        // When
        // Then
        assertThat(result).isNotNull();
        assertThat(hostNotFoundException.getMessage()).isEqualTo(result.getDescription());
    }

    @Test
    void itShouldHandleRequestNotFoundExc() {
        // Given
        RequestNotFound requestNotFound = new RequestNotFound("Request not found");
        ErrorResponse result = errorHandler.notFoundExc(requestNotFound);
        // When
        // Then
        assertThat(result).isNotNull();
        assertThat(requestNotFound.getMessage()).isEqualTo(result.getDescription());
    }

    @Test
    void itShouldHandleValidationExc() {
        // Given
        ValidationException validationException = new ValidationException("Validation exception");
        ErrorResponse result = errorHandler.annotationValidationExc(validationException);
        // When
        // Then
        assertThat(result).isNotNull();
        assertThat(validationException.getMessage()).isEqualTo(result.getDescription());
    }

    @Test
    void itShouldHandleBookingValidationExc() {
        // Given
        ItemNotAvailableException itemNotAvailableException = new ItemNotAvailableException("Item not available exception");
        ErrorResponse result = errorHandler.bookingValidationExc(itemNotAvailableException);
        // When
        // Then
        assertThat(result).isNotNull();
        assertThat(itemNotAvailableException.getMessage()).isEqualTo(result.getDescription());
    }



}