package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание запроса пользователем ID: {}", userId);
        return requestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение запросов пользователя ID: {}", userId);
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех запросов для пользователя ID: {}", userId);
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("Получение запроса пользователем ID: {}", userId);
        return requestService.getRequestById(userId, requestId);
    }
}
