package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    @NotNull
    private String authorName;
    @NotNull
    private LocalDateTime created;
}
