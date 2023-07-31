package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedAsc(long requestorId);

    @Query("select ir " +
            "from ItemRequest as ir " +
            "where ir.requestor.id <> ?1")
    Page<ItemRequest> findAllUserRequest(long userId, Pageable pageable);
}
