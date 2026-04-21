package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto createBooking(Long bookerId, BookingDto bookingDto);

    BookingDto updateStatus(Long bookingId, Boolean approved, Long ownerId);

    BookingDto getBookingById(Long bookingId, Long userId);

    Collection<BookingDto> getUserBookings(Long userId, String state);

    Collection<BookingDto> getOwnerBookings(Long ownerId, String state);
}
