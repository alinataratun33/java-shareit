package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    private ItemRequest getRequestOrThrow(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден с ID: " + requestId));
    }

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.debug("Создание запроса для пользователя с ID: {}", userId);
        User requester = getUserOrThrow(userId);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        ItemRequest savedItemRequest = requestRepository.save(itemRequest);

        log.info("Запрос создан с ID: {}", savedItemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests(Long userId) {
        log.debug("Получение всех запросов других пользователей для пользователя ID: {}", userId);
        getUserOrThrow(userId);

        List<ItemRequest> requests = requestRepository.findByRequesterIdNotOrderByCreatedDesc(userId);

        return addItems(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.debug("Поиск запроса с ID: {}", requestId);
        ItemRequest itemRequest = getRequestOrThrow(requestId);
        getUserOrThrow(userId);

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);

        List<ItemDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        dto.setItems(items);

        return dto;
    }

    @Override
    public Collection<ItemRequestDto> getUserRequests(Long userId) {
        log.debug("Получение запросов пользователя ID: {}", userId);
        getUserOrThrow(userId);

        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        return addItems(requests);
    }

    private List<ItemRequestDto> addItems(List<ItemRequest> requests) {
        List<Long> ids = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> allItems = itemRepository.findByRequestIdIn(ids);

        Map<Long, List<ItemDto>> itemsByRequest = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemDto, Collectors.toList())
                ));

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
                    dto.setItems(itemsByRequest.getOrDefault(request.getId(), List.of()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
