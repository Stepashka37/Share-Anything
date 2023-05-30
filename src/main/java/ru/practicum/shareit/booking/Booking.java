package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Builder
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @Column(name = "start_date")
    private  LocalDateTime start;

    @Column(name = "end_date")
    private  LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id",
    referencedColumnName = "id")
    private  Item item;

    @OneToOne
    @JoinColumn(name = "booker_id",
            referencedColumnName = "id")
    private  User booker;

    @Enumerated(EnumType.STRING)
    private  BookingStatus status;

    public Booking(long id, LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }

    public Booking() {

    }
}

