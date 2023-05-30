package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long userId);


    @Query("select it " +
            "from Item as it " +
            "where ((lower(it.name) like %:text% " +
            "or lower(it.description) like %:text%)" +
            "and it.isAvailable is true) ")
    List<Item> findAllByText(@Param("text") String text);
}
