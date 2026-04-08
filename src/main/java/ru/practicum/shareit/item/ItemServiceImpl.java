package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User getUserOrThrow(Long ownerId) {
        return userRepository.getById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + ownerId));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена с ID: " + itemId));
    }

    @Override
    public Collection<ItemDto> getAllItemsByOwner(Long ownerId) {
        log.info("Получение всех вещей владельца c ID: {}", ownerId);

        getUserOrThrow(ownerId);

        return itemRepository.getAllItemsByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        log.info("Создание новой вещи для пользователя c ID: {}", ownerId);

        User owner = getUserOrThrow(ownerId);

        Item item = ItemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.createItem(item);

        log.info("Вещь успешно создана с ID: {} для пользователя c ID: {}", savedItem.getId(), ownerId);

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItemById(Long id) {
        log.info("Поиск вещи по ID: {}", id);

        Item item = getItemOrThrow(id);

        log.info("Вещь найдена: ID={}, name={}", item.getId(), item.getName());
        return ItemMapper.toItemDto(item);
    }

    @Override
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

        itemRepository.updateItem(existingItem);
        log.info("Вещь c ID: {} успешно обновлена пользователем c ID: {}", id, ownerId);

        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public Collection<ItemDto> searchItem(String text) {
        log.info("Поиск вещей по тексту: '{}'", text);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItem(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
