package ru.practicum.shareit.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTest {

    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Нужен фотоаппарат");
        requestDto.setCreated(LocalDateTime.of(2026, 4, 15, 10, 30, 0));
        requestDto.setRequesterId(5L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(10L);
        itemDto.setName("Фотоаппарат");
        requestDto.setItems(List.of(itemDto));

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужен фотоаппарат");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-04-15T10:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Фотоаппарат");
    }

}
