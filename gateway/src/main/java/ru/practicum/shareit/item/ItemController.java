package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на получение всех вещей владельца с ID: {}", ownerId);
        return itemClient.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Поиск вещей по тексту: '{}'", text);
        return itemClient.searchItem(text);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @Valid @RequestBody ItemDto item) {
        log.info("Запрос на создание вещи для владельца ID: {}", ownerId);
        return itemClient.createItem(ownerId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable("itemId") Long id,
                                             @RequestBody ItemDto item) {

        log.info("Запрос на обновление вещи {} владельцем ID: {}", id, ownerId);
        return itemClient.updateItem(id, ownerId, item);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                                @PathVariable("itemId") Long id,
                                                @RequestBody CommentDto commentDto) {
        log.info("Запрос на создание комментария пользователем {} к вещи {}", authorId, id);
        return itemClient.createComment(authorId, id, commentDto);
    }
}