package ru.practicum.shareit.integration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.WAITING;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {

        UserDto owner = new UserDto();
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");
        ownerId = userService.createUser(owner).getId();


        UserDto booker = new UserDto();
        booker.setName("Арендатор");
        booker.setEmail("booker@example.com");
        bookerId = userService.createUser(booker).getId();


        ItemDto itemDto = new ItemDto();
        itemDto.setName("Фотоаппарт");
        itemDto.setDescription("Зеркальный фотоаппарт");
        itemDto.setAvailable(true);
        itemId = itemService.createItem(ownerId, itemDto).getId();


        bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(3));
    }

    @Test
    void testSaveBookingToDatabase() {
        BookingDto savedBooking = bookingService.createBooking(bookerId, bookingDto);

        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getItem().getId()).isEqualTo(itemId);
        assertThat(savedBooking.getBooker().getId()).isEqualTo(bookerId);
        assertThat(savedBooking.getStatus()).isEqualTo(WAITING);
    }

    @Test
    void testUpdateStatus() {
        BookingDto savedBooking = bookingService.createBooking(bookerId, bookingDto);

        BookingDto updatedBooking = bookingService.updateStatus(savedBooking.getId(), true, ownerId);

        assertThat(updatedBooking.getStatus()).isEqualTo(APPROVED);
    }

    @Test
    void testGetBookingById() {
        BookingDto savedBooking = bookingService.createBooking(bookerId, bookingDto);
        BookingDto foundBooking = bookingService.getBookingById(savedBooking.getId(), bookerId);

        assertThat(foundBooking.getId()).isEqualTo(savedBooking.getId());
        assertThat(foundBooking.getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void testGetUserBookings() {
        bookingService.createBooking(bookerId, bookingDto);

        BookingDto secondBooking = new BookingDto();
        secondBooking.setItemId(itemId);
        secondBooking.setStart(LocalDateTime.now().plusDays(5));
        secondBooking.setEnd(LocalDateTime.now().plusDays(7));
        bookingService.createBooking(bookerId, secondBooking);

        var bookings = bookingService.getUserBookings(bookerId, "ALL");

        assertThat(bookings.size()).isEqualTo(2);
    }

    @Test
    void testGetOwnerBookings() {
        bookingService.createBooking(bookerId, bookingDto);

        var bookings = bookingService.getOwnerBookings(ownerId, "ALL");

        assertThat(bookings.size()).isEqualTo(1);
    }
}
