package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingDtoToCreate {

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent

    private LocalDateTime end;

    private long itemId;

    BookingDtoToCreate(@NotNull @FutureOrPresent LocalDateTime start, @NotNull @FutureOrPresent LocalDateTime end, long itemId) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
    }

    }

