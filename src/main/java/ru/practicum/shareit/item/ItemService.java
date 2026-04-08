package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllItemsByOwner(Long ownerId);

    ItemDto createItem(Long ownerId, ItemDto itemDto);

    ItemDto getItemById(Long id);

    ItemDto updateItem(Long id, Long ownerId, ItemDto itemDto);

    Collection<ItemDto> searchItem(String text);
}
