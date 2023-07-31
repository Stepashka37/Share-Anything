package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .owner(new User())
                .description("description")
                .isAvailable(true)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.of(2023, 01, 01, 10, 00, 00))
                .end(LocalDateTime.of(2023, 01, 01, 11, 00, 00))
                .status(BookingStatus.APPROVED)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-01T11:00:00");

    }


}