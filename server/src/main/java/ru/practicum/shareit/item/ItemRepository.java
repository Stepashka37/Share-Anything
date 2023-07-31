package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerIdOrderByIdAsc(long userId, Pageable pageable);

    @Query("select it " +
            "from Item as it " +
            "where ((lower(it.name) like %:text% " +
            "or lower(it.description) like %:text%)" +
            "and it.isAvailable is true) ")
    Page<Item> findAllByText(@Param("text") String text, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> requests);

    List<Item> findAllByRequestId(long requestId);

}
