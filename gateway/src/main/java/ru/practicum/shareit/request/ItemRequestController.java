package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание запроса пользователем ID: {}", userId);
        return requestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение запросов пользователя ID: {}", userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех запросов для пользователя ID: {}", userId);
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Получение запроса пользователем ID: {}", userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
