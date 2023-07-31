package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 and " +
            "b.status = 'APPROVED'")
    List<Booking> findAllBookingsByItemId(long itemId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join b.booker as u " +
            "where i.id = ?1 " +
            "and u.id = ?2 " +
            "and b.end < ?3 ")
    List<Booking> findAllByBookerIdAndItemIdAndEndBefore(long itemId, long bookerId,  LocalDateTime end);

    Page<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = ?1  and ?2 " +
            "between b.start  and b.end  " +
            "order by b.start desc")
    Page<Booking> findAllCurrentBookingsByUser(long id, LocalDateTime dateTim, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = ?1  and b.start > ?2 " +
            "order by b.start desc")
    Page<Booking> findAllFutureBookingsByUser(long userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = ?1  and b.end < ?2 " +
            "order by b.start desc")
    Page<Booking> findAllPastBookingsByUser(long userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = ?1  and b.status = 'REJECTED'" +
            "order by b.start desc")
    Page<Booking> findAllRejectedBookingsByUser(long userId, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = ?1  and b.status = 'WAITING'" +
            "order by b.start desc")
    Page<Booking> findAllWaitingBookingsByUser(long userId, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "order by b.start desc")
    Page<Booking> findAllItemsBookings(Long userId, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and ?2 between b.start  and b.end " +
            "order by b.start desc")
    Page<Booking> findAllItemsCurrentBookings(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    Page<Booking> findAllItemsPastBookings(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    Page<Booking> findAllItemsFutureBookings(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.status = 'REJECTED' " +
            "order by b.start desc")
    Page<Booking> findAllItemsRejectedBookings(Long userId, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.status = 'WAITING' " +
            "order by b.start desc")
    Page<Booking> findAllItemsWaitingBookings(Long userId, Pageable pageable);
}

