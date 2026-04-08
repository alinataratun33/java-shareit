package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Collection<Item> getAllItemsByOwner(Long ownerId);

    Optional<Item> getItemById(Long id);

    Item createItem(Item item);

    Item updateItem(Item item);

    Collection<Item> searchItem(String text);
}
