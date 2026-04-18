package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime startBefore, LocalDateTime endAfter);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime endBefore);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime startAfter);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
            Long itemId, Status status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, Status status, LocalDateTime now);
}

