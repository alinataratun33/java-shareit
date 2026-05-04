package ru.practicum.shareit.request;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@NoArgsConstructor
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(request.getId());
        itemRequestDto.setDescription(request.getDescription());
        itemRequestDto.setRequesterId(request.getRequester().getId());
        itemRequestDto.setCreated(request.getCreated());
        return itemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto, User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}
