package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Data
@Builder
public class Booking {
    private final long bookingId;

    private final LocalDateTime start;

    private final LocalDateTime finish;

    private final Item item;

    private final User booker;

    private final BookingStatus status;

}

