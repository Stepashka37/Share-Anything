package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingDtoForItemHost {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private long bookerId;

    BookingDtoForItemHost(long id, LocalDateTime start, LocalDateTime end, long bookerId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.bookerId = bookerId;
    }

}
