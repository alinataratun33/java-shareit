package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Long requestId;
}
