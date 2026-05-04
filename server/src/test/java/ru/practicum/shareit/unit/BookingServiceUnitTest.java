package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);

        now = LocalDateTime.now();

        owner = new User();
        owner.setId(1L);
        owner.setName("Владелец");

        booker = new User();
        booker.setId(2L);
        booker.setName("Арендатор");

        item = new Item();
        item.setId(1L);
        item.setName("Фотоаппарат");
        item.setOwner(owner);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setStart(now.plusDays(1));
        booking.setEnd(now.plusDays(3));

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(3));
    }

    @Test
    void createBookingWithItemNotAvailable() {
        item.setAvailable(false);

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(2L, bookingDto);
        });
        assertEquals("Эта вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void createBookingByOwner() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(1L, bookingDto);
        });
        assertEquals("Владелец не может забронировать свою вещь", exception.getMessage());
    }

    @Test
    void updateStatusByNonOwner() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            bookingService.updateStatus(1L, true, 99L);
        });
        assertEquals("Только владелец может одобрить/отклонить бронирование", exception.getMessage());
    }

    @Test
    void getUserBookingsAllState() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L)).thenReturn(List.of(booking));

        var bookings = bookingService.getUserBookings(2L, "ALL");

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.iterator().next().getId());
    }

    @Test
    void getUserBookingsCurrentState() {
        booking.setStart(now.minusDays(1));
        booking.setEnd(now.plusDays(1));

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(2L), any(), any()))
                .thenReturn(List.of(booking));

        var bookings = bookingService.getUserBookings(2L, "CURRENT");

        assertEquals(1, bookings.size());
    }

    @Test
    void getUserBookingsPastState() {
        booking.setEnd(now.minusDays(1));

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(eq(2L), any()))
                .thenReturn(List.of(booking));

        var bookings = bookingService.getUserBookings(2L, "PAST");

        assertEquals(1, bookings.size());
    }

    @Test
    void getUserBookingsFutureState() {
        booking.setStart(now.plusDays(1));

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(eq(2L), any()))
                .thenReturn(List.of(booking));

        var bookings = bookingService.getUserBookings(2L, "FUTURE");

        assertEquals(1, bookings.size());
    }

    @Test
    void getUserBookingsWaitingState() {
        booking.setStatus(Status.WAITING);

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(eq(2L), eq(Status.WAITING)))
                .thenReturn(List.of(booking));

        var bookings = bookingService.getUserBookings(2L, "WAITING");

        assertEquals(1, bookings.size());
        assertEquals(Status.WAITING, bookings.iterator().next().getStatus());
    }

    @Test
    void getUserBookingsRejectedState() {
        booking.setStatus(Status.REJECTED);

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(eq(2L), eq(Status.REJECTED)))
                .thenReturn(List.of(booking));

        var bookings = bookingService.getUserBookings(2L, "REJECTED");

        assertEquals(1, bookings.size());
        assertEquals(Status.REJECTED, bookings.iterator().next().getStatus());
    }

    @Test
    void getUserBookingsInvalidState() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class,
                () -> bookingService.getUserBookings(2L, "INVALID_STATE"));
    }
}