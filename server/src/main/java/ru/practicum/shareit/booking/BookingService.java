package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(long userId, BookingDtoToCreate bookingDto);

    BookingDto approveBooking(long userId, long bookingId, boolean approved);

    BookingDto getBooking(long userId, long bookingDto);

    List<BookingDto> getUserBookings(long userId, String state, Integer from, Integer size);

    List<BookingDto> getAllUserItemsBookings(long userId, String state, Integer from, Integer size);
}
