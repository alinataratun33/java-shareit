package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на получение всех вещей владельца с ID: {}", ownerId);
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("Поиск вещей по тексту: '{}'", text);
        return itemService.searchItem(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @Valid @RequestBody ItemDto item) {
        log.info("Запрос на создание вещи для владельца ID: {}", ownerId);
        return itemService.createItem(ownerId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable("itemId") Long id,
                              @RequestBody ItemDto item) {

        log.info("Запрос на обновление вещи {} владельцем ID: {}", id, ownerId);
        return itemService.updateItem(id, ownerId, item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                    @PathVariable("itemId") Long id,
                                    @RequestBody CommentDto commentDto) {
        log.info("Запрос на создание комментария пользователем {} к вещи {}", authorId, id);
        return itemService.createComment(authorId, id, commentDto);
    }
}