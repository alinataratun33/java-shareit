package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private Long itemId;
    private Status status;
    private UserDto booker;
    private ItemDto item;
}
