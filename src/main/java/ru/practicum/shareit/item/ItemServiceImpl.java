package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingShortDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена с ID: " + itemId));
    }

    @Override
    public Collection<ItemDto> getAllItemsByOwner(Long ownerId) {
        log.info("Получение всех вещей владельца c ID: {}", ownerId);

        getUserOrThrow(ownerId);
        LocalDateTime now = LocalDateTime.now();

        return itemRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);

                    dto.setComments(commentRepository.findByItemId(item.getId())
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList()));


                    bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                                    item.getId(), Status.APPROVED, now)
                            .ifPresent(booking -> dto.setLastBooking(toBookingShortDto(booking)));

                    bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                                    item.getId(), Status.APPROVED, now)
                            .ifPresent(booking -> dto.setNextBooking(toBookingShortDto(booking)));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        log.info("Создание новой вещи для пользователя c ID: {}", ownerId);

        User owner = getUserOrThrow(ownerId);

        Item item = ItemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);

        log.info("Вещь успешно создана с ID: {} для пользователя c ID: {}", savedItem.getId(), ownerId);

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        log.info("Поиск вещи по ID: {}", id);

        getUserOrThrow(userId);

        Item item = getItemOrThrow(id);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        itemDto.setComments(commentRepository.findByItemId(id)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                            item.getId(), Status.APPROVED, now)
                    .ifPresent(booking -> itemDto.setLastBooking(toBookingShortDto(booking)));

            bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(), Status.APPROVED, now)
                    .ifPresent(booking -> itemDto.setNextBooking(toBookingShortDto(booking)));
        }
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long id, Long ownerId, ItemDto itemDto) {
        log.info("Обновление вещи c ID: {} пользователем c ID: {}", id, ownerId);

        Item existingItem = getItemOrThrow(id);

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Только владелец может редактировать вещь");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(existingItem);
        log.info("Вещь c ID: {} успешно обновлена пользователем c ID: {}", id, ownerId);

        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public Collection<ItemDto> searchItem(String text) {
        log.info("Поиск вещей по тексту: '{}'", text);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long authorId, Long itemId, CommentDto commentDto) {
        log.info("Создание комментария: authorId={}, itemId={}, text={}",
                authorId, itemId, commentDto.getText());
        Item item = getItemOrThrow(itemId);
        User user = getUserOrThrow(authorId);

        LocalDateTime now = LocalDateTime.now();
        boolean hasFinishedBooking = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                authorId, itemId, now);

        if (!hasFinishedBooking) {
            throw new ValidationException("Пользователь не может оставить комментарий к этой вещи, " +
                    "поскольку он еще не завершил бронирование");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user);
        comment.setCreated(now);

        Comment savedComment = commentRepository.save(comment);

        log.info("Комментарий успешно создан: id={}, authorId={}, itemId={}", savedComment.getId(), authorId, itemId);
        return CommentMapper.toCommentDto(savedComment);
    }
}
