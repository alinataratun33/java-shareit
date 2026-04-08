package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long currentId = 1;

    @Override
    public Collection<Item> getAllItemsByOwner(Long ownerId) {
        log.debug("Запрос всех вещей владельца ID: {}", ownerId);
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        log.debug("Поиск вещи по ID: {}", id);
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item createItem(Item item) {
        log.debug("Создание новой вещи");
        item.setId(currentId++);
        items.put(item.getId(), item);
        log.debug("Вещь создана с ID: {}", item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        log.debug("Обновление вещи");
        items.put(item.getId(), item);
        log.debug("Вещь с ID {} успешно обновлена", item.getId());
        return item;
    }

    @Override
    public Collection<Item> searchItem(String text) {
        log.info("Поиск вещей по тексту: '{}'", text);

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) ||
                                (item.getDescription() != null &&
                                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                )
                .collect(Collectors.toList());
    }
}
