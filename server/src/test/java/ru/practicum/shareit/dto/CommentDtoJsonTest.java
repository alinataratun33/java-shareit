package ru.practicum.shareit.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentDtoJsonTest {

    private final JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Отличная вещь!");
        commentDto.setAuthorName("Иван Петров");
        commentDto.setCreated(LocalDateTime.of(2026, 4, 15, 10, 30, 0));

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Иван Петров");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-04-15T10:30:00");
    }
}
