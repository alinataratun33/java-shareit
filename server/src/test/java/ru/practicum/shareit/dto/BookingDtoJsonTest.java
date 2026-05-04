package ru.practicum.shareit.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {

    private final JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2026, 4, 15, 10, 30, 0));
        bookingDto.setEnd(LocalDateTime.of(2026, 4, 20, 18, 0, 0));
        bookingDto.setItemId(5L);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-04-15T10:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-04-20T18:00:00");
    }
}
