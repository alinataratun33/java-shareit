package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    private ItemServiceImpl itemService;
    private User owner;
    private User booker;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository,
                commentRepository, bookingRepository, requestRepository);

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

        now = LocalDateTime.now();
    }

    @Test
    void testCreateComment() {
        User booker = new User();
        booker.setId(2L);
        booker.setName("Арендатор");

        Item item = new Item();
        item.setId(1L);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отличная вещь!");

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(eq(2L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(true);

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText("Отличная вещь!");
        savedComment.setAuthor(booker);
        savedComment.setItem(item);
        savedComment.setCreated(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.createComment(2L, 1L, commentDto);

        assertNotNull(result);
        assertEquals("Отличная вещь!", result.getText());
    }

    @Test
    void updateItemByNonOwner() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Новое имя");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Exception exception = assertThrows(ForbiddenException.class, () -> {
            itemService.updateItem(1L, 99L, itemDto);
        });
        assertEquals("Только владелец может редактировать вещь", exception.getMessage());
    }

    @Test
    void getAllItemsByOwner_shouldSetLastAndNextBooking() {
        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setItem(item);
        lastBooking.setBooker(booker);
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setStart(now.minusDays(10));
        lastBooking.setEnd(now.minusDays(5));

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setItem(item);
        nextBooking.setBooker(booker);
        nextBooking.setStatus(Status.APPROVED);
        nextBooking.setStart(now.plusDays(5));
        nextBooking.setEnd(now.plusDays(10));

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerIdOrderByIdAsc(1L)).thenReturn(List.of(item));
        when(commentRepository.findByItemIdInOrderByCreatedDesc(any())).thenReturn(List.of());
        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(any(), eq(Status.APPROVED)))
                .thenReturn(List.of(lastBooking, nextBooking));

        List<ItemDto> result = (List<ItemDto>) itemService.getAllItemsByOwner(1L);

        assertNotNull(result.get(0).getLastBooking());
        assertNotNull(result.get(0).getNextBooking());
    }
}