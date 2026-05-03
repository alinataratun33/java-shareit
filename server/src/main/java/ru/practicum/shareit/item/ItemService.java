package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllItemsByOwner(Long ownerId);

    ItemDto createItem(Long ownerId, ItemDto itemDto);

    ItemDto getItemById(Long id, Long userId);

    ItemDto updateItem(Long id, Long ownerId, ItemDto itemDto);

    Collection<ItemDto> searchItem(String text);

    CommentDto createComment(Long authorId, Long itemId, CommentDto commentDto);
}
