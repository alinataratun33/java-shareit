package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена с ID: " + itemId));
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено с ID: " + bookingId));
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long bookerId, BookingDto bookingDto) {
        log.info("Создание бронирования: bookerId={}, itemId={}", bookerId, bookingDto.getItemId());

        User booker = getUserOrThrow(bookerId);
        Item item = getItemOrThrow(bookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Эта вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        log.info("Бронирование создано с ID: {}", savedBooking.getId());
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long bookingId, Boolean approved, Long ownerId) {
        log.info("Обновление статуса бронирования: bookingId={}, approved={}, ownerId={}",
                bookingId, approved, ownerId);
        Booking exitingBooking = getBookingOrThrow(bookingId);

        if (!exitingBooking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Только владелец может одобрить/отклонить бронирование");
        }

        if (exitingBooking.getStatus() != Status.WAITING) {
            throw new ValidationException("Бронирование должно быть в статусе WAITING");
        }

        if (approved) {
            exitingBooking.setStatus(Status.APPROVED);
        } else {
            exitingBooking.setStatus(Status.REJECTED);
        }

        bookingRepository.save(exitingBooking);

        log.info("Статус бронирования {} обновлён", bookingId);
        return BookingMapper.toBookingDto(exitingBooking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.info("Поиск бронирования по ID {}", bookingId);

        Booking booking = getBookingOrThrow(bookingId);
        getUserOrThrow(userId);

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("Только владелец вещи и автор бронирования могут получить доступ");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getUserBookings(Long userId, String stateParam) {
        log.info("Получение бронирований пользователя: userId={}, state={}", userId, stateParam);

        getUserOrThrow(userId);
        State state = parseState(stateParam);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };

        log.info("Найдено {} бронирований для пользователя {}", bookings.size(), userId);
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getOwnerBookings(Long ownerId, String stateParam) {
        log.info("Получение бронирований вещей владельца: ownerId={}, state={}", ownerId, stateParam);

        getUserOrThrow(ownerId);
        State state = parseState(stateParam);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    ownerId, now, now);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
        };

        log.info("Найдено {} бронирований для вещей владельца {}", bookings.size(), ownerId);
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private State parseState(String stateParam) {
        try {
            return State.valueOf(stateParam.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неизвестное состояние: " + stateParam);
        }
    }
}
