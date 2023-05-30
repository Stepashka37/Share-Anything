package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@Builder
public class BookingDto {

    private long id;

    @NotNull
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;

    private Item item;

    private User booker;

    private BookingStatus status;
}
