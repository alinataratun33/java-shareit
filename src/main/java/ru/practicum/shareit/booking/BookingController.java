package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                    @Valid @RequestBody BookingDto bookingDto) {

        log.info("Запрос на создание бронирования пользователем: bookerId={}, itemId={}",
                bookerId, bookingDto.getItemId());
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                   @PathVariable Long bookingId, @RequestParam Boolean approved) {

        log.info("Запрос на обновление статуса бронирования пользователем: ownerId={}", ownerId);
        return bookingService.updateStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {

        log.info("Запрос бронирования пользователем: userId={}", userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {

        log.info("Запрос бронирований пользователя: userId={}", userId);
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @RequestParam(defaultValue = "ALL") String state) {

        log.info("Запрос бронирований вещей владельца: ownerId={}", ownerId);
        return bookingService.getOwnerBookings(ownerId, state);
    }
}
