package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findByOwnerIdOrderByIdAsc(Long ownerId);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> requestIds);

    @Query("SELECT i FROM Item i " +
            "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))) " +
            "AND i.available = true")
    Collection<Item> searchByText(String text);
}
